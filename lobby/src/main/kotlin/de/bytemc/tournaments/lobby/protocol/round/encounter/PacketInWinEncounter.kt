package de.bytemc.tournaments.lobby.protocol.round.encounter

import de.bytemc.tournaments.api.BooleanResult
import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.TournamentLobbyPlugin
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nico_ND1
 */
class PacketInWinEncounter : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<BooleanResult> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)
        if (tournament == null) {
            buffer.release()
            return failure(NullPointerException("Couldn't find tournament for $id"))
        }

        val round = buffer.readInt()
        if (tournament.currentRound()?.count != round) {
            buffer.release()
            return success(BooleanResult.FALSE)
        }

        return findEncounter(tournament)
    }

    private fun findEncounter(tournament: ITournament): ICommunicationPromise<BooleanResult> {
        val encounterID = buffer.readInt()
        val round = tournament.currentRound()

        if (round != null) {
            for (encounter in round.encounters) {
                if (encounter.id == encounterID) {
                    return findTeam(tournament, encounter)
                }
            }
        }

        return success(BooleanResult.FALSE)
    }

    private fun findTeam(
        tournament: ITournament,
        encounter: TournamentEncounter,
    ): ICommunicationPromise<BooleanResult> {
        val teamID = buffer.readInt()

        val plugin = JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)
        plugin.collectives.handleEncounterWin(encounter)

        return when {
            encounter.firstTeam.id == teamID -> {
                encounter.winnerTeam = encounter.firstTeam
                success(BooleanResult.TRUE)
            }
            encounter.secondTeam.id == teamID -> {
                encounter.winnerTeam = encounter.secondTeam
                success(BooleanResult.TRUE)
            }
            else -> {
                failure(NullPointerException("Couldn't find winner team for $tournament $encounter $teamID"))
            }
        }
    }
}
