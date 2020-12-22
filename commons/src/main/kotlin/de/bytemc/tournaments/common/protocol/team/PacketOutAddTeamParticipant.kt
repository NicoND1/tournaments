package de.bytemc.tournaments.common.protocol.team

import de.bytemc.tournaments.api.*
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutAddTeamParticipant(tournament: ITournament, team: TournamentTeam, participant: TournamentParticipant) :
    BytePacket() {

    init {
        writeUUID(tournament.id())
        buffer.writeInt(team.id)
        writeUUID(participant.uuid)
        writeString(participant.name)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }

}
