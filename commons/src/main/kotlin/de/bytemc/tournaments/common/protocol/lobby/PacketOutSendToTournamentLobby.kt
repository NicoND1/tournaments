package de.bytemc.tournaments.common.protocol.lobby

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutSendToTournamentLobby(tournament: ITournament, participant: TournamentParticipant) : BytePacket() {

    init {
        writeUUID(tournament.id())
        writeUUID(participant.uuid)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
