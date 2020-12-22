package de.bytemc.tournaments.lobby.protocol.round

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInStartRound : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val tournamentID = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(tournamentID)
            ?: return failure(NullPointerException("Couldn't find tournament $tournamentID"))

        val roundCount = buffer.readInt()
        val encounters = readEncounters(tournament)

        tournament.currentRound = TournamentRound(roundCount, encounters)
        return unit()
    }

    private fun readEncounters(tournament: ITournament): Array<TournamentEncounter> {
        val size = buffer.readInt()
        val result: Array<TournamentEncounter?> = arrayOfNulls(size)

        for (i in 0..size) {
            val id = buffer.readInt()
            val firstTeam = tournament.findTeam(buffer.readInt())
            val secondTeam = tournament.findTeam(buffer.readInt())
            var winnerTeam: TournamentTeam? = null

            if (buffer.readBoolean()) winnerTeam = tournament.findTeam(buffer.readInt())
            result[i] = TournamentEncounter(id, firstTeam!!, secondTeam!!, winnerTeam)
        }

        return result.map { it!! }.toTypedArray()
    }

}
