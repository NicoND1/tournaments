package de.bytemc.tournaments.lobby.protocol.round.encounter

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInWinEncounter : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Boolean> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)

        return if (tournament == null) {
            failure(NullPointerException("Couldn't find tournament for $id"))
        } else {
            findEncounter(tournament)
        }
    }

    private fun findEncounter(tournament: ITournament): ICommunicationPromise<Boolean> {
        val encounterID = buffer.readInt()
        val round = tournament.currentRound()

        if (round != null) {
            for (encounter in round.encounters) {
                if (encounter.id == encounterID) {
                    return findTeam(tournament, encounter)
                }
            }
        }

        return success(false)
    }

    private fun findTeam(tournament: ITournament, encounter: TournamentEncounter): ICommunicationPromise<Boolean> {
        val teamID = buffer.readInt()

        return when {
            encounter.firstTeam.id == teamID -> {
                encounter.winnerTeam = encounter.firstTeam
                success(true)
            }
            encounter.secondTeam.id == teamID -> {
                encounter.winnerTeam = encounter.secondTeam
                success(true)
            }
            else -> {
                failure(NullPointerException("Couldn't find winner team for $tournament $encounter $teamID"))
            }
        }
    }
}
