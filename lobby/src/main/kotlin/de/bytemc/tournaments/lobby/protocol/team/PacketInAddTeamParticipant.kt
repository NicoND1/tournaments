package de.bytemc.tournaments.lobby.protocol.team

import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.api.readString
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class PacketInAddTeamParticipant : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(addToTournament(tournament))
        }
        return success(false)
    }

    private fun addToTournament(tournament: LobbyTournament): Boolean {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return addToTeam(team)
            }
        }

        return false
    }

    private fun addToTeam(team: TournamentTeam): Boolean {
        val participant = TournamentParticipant(readUUID(), readString())
        if (team.participants.contains(participant)) {
            return false
        }

        team.participantsLock.withLock { team.participants.add(participant) }
        return true
    }

}
