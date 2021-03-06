package de.bytemc.tournaments.lobby.protocol.round

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.TournamentLobbyPlugin
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nico_ND1
 */
class PacketInStartRound : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val tournamentID = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(tournamentID)
            ?: return failure(NullPointerException("Couldn't find tournament $tournamentID"))

        val roundCount = buffer.readInt()
        val encounters = readEncounters(tournament)
        val round = TournamentRound(roundCount, encounters)

        round.parentRound = tournament.currentRound
        tournament.currentRound = round
        //tournament.pairings = TournamentPairings(tournament)

        val plugin = JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)
        plugin.collectives.handleRoundStart(tournament)
        return unit()
    }

    private fun readEncounters(tournament: ITournament): Array<TournamentEncounter> {
        val size = buffer.readInt()
        val result: Array<TournamentEncounter?> = arrayOfNulls(size)

        for (i in 0 until size) {
            val id = buffer.readInt()
            val firstTeamID = buffer.readInt()
            val secondTeamID = buffer.readInt()
            val firstTeam = tournament.findTeam(firstTeamID)
            val secondTeam = tournament.findTeam(secondTeamID)
            var winnerTeam: TournamentTeam? = null

            if (buffer.readBoolean()) winnerTeam = tournament.findTeam(buffer.readInt())
            result[i] = TournamentEncounter(id, firstTeam!!, secondTeam!!, winnerTeam)
        }

        return result.map { it!! }.toTypedArray()
    }

}
