package de.bytemc.tournaments.server.protocol.team

import de.bytemc.tournaments.api.BooleanResult
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.common.protocol.team.PacketOutRemoveTeamParticipant
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class PacketInRemoveTeamParticipant : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<BooleanResult> {
        val id = readUUID()
        val tournament = ServerTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(removeFromTournament(tournament, connection))
        }
        return success(BooleanResult.FALSE)
    }

    private fun removeFromTournament(tournament: ServerTournament, connection: IConnection): BooleanResult {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return removeFromTeam(tournament, team, connection)
            }
        }

        return BooleanResult.FALSE
    }

    private fun removeFromTeam(
        tournament: ServerTournament,
        team: TournamentTeam,
        connection: IConnection,
    ): BooleanResult {
        val participantID = readUUID()

        var participant: TournamentParticipant? = null
        for (participants in team.participants) {
            if (participants.uuid == participantID) {
                participant = participants
            }
        }

        if (participant == null) {
            return BooleanResult.FALSE
        } else {
            team.participantsLock.withLock { team.participants.remove(participant) }
        }

        notifyExcept(tournament, team, participant, connection)
        return BooleanResult.TRUE
    }

    private fun notifyExcept(
        tournament: ServerTournament,
        team: TournamentTeam,
        participant: TournamentParticipant,
        connection: IConnection,
    ) {
        val packet = PacketOutRemoveTeamParticipant(tournament, team, participant)
        tournament.sendUnitPacket(packet, connection)
    }

}
