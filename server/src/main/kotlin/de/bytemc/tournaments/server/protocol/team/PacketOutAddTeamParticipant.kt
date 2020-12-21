package de.bytemc.tournaments.server.protocol.team

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.writeString
import de.bytemc.tournaments.server.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.nio.charset.StandardCharsets

/**
 * @author Nico_ND1
 */
class PacketOutAddTeamParticipant : BytePacket {

    constructor(tournament: ITournament, team: TournamentTeam, participant: TournamentParticipant) {
        writeUUID(tournament.id())
        buffer.writeInt(team.id)
        writeUUID(participant.uuid)
        writeString(participant.name)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }
}
