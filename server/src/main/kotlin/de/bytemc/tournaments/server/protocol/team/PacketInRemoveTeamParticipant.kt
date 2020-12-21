package de.bytemc.tournaments.server.protocol.team

import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.readUUID
import de.bytemc.tournaments.server.sendUnitPacket
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class PacketInRemoveTeamParticipant : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val id = readUUID()
        val tournament = ServerTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(removeFromTournament(tournament, connection))
        }
        return success(false)
    }

    private fun removeFromTournament(tournament: ServerTournament, connection: IConnection): Boolean {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return removeFromTeam(tournament, team, connection)
            }
        }

        return false
    }

    private fun removeFromTeam(tournament: ServerTournament, team: TournamentTeam, connection: IConnection): Boolean {
        val participantID = readUUID()

        var participant: TournamentParticipant? = null
        for (participants in team.participants) {
            if (participants.uuid == participantID) {
                participant = participants
            }
        }

        if (participant == null) {
            return false
        } else {
            team.participantsLock.withLock { team.participants.remove(participant) }
        }

        notifyExcept(tournament, team, participant, connection)
        return true
    }

    private fun notifyExcept(
        tournament: ServerTournament,
        team: TournamentTeam,
        participant: TournamentParticipant,
        connection: IConnection
    ) {
        val packet = PacketOutRemoveTeamParticipant(tournament, team, participant)
        tournament.sendUnitPacket(packet, connection)
    }
}
