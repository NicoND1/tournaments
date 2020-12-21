package de.bytemc.tournaments.server.protocol.team

import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.*
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
        val tournament = ServerTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(addToTournament(tournament, connection))
        }
        return success(false)
    }

    private fun addToTournament(tournament: ServerTournament, connection: IConnection): Boolean {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return addToTeam(tournament, team, connection)
            }
        }

        return false
    }

    private fun addToTeam(tournament: ServerTournament, team: TournamentTeam, connection: IConnection): Boolean {
        val participant = TournamentParticipant(readUUID(), readString())
        if (team.participants.contains(participant)) {
            return false
        }

        team.participantsLock.withLock { team.participants.add(participant) }
        notifyExcept(tournament, team, participant, connection)
        return true
    }

    private fun notifyExcept(
        tournament: ServerTournament,
        team: TournamentTeam,
        participant: TournamentParticipant,
        connection: IConnection
    ) {
        val packet = PacketOutAddTeamParticipant(tournament, team, participant)
        tournament.sendUnitPacket(packet, connection)
    }
}
