package de.bytemc.tournaments.server.protocol.team

import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.writeString
import de.bytemc.tournaments.api.writeUUID
import de.bytemc.tournaments.server.ServerTournament
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutTeamMembers(tournament: ServerTournament) : BytePacket() {

    init {
        writeUUID(tournament.id())

        val teams = tournament.teams()
        buffer.writeInt(teams.size)
        for (team in teams) {
            buffer.writeInt(team.id)

            val participants = team.participants
            buffer.writeInt(participants.size)
            participants.forEach { writeParticipant(it) }
        }
    }

    private fun writeParticipant(participant: TournamentParticipant) {
        writeUUID(participant.uuid)
        writeString(participant.name)

        buffer.writeBoolean(participant.texture != null)
        if (participant.texture != null) {
            writeString(participant.texture!!.value)
            writeString(participant.texture!!.signature)
        }
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
