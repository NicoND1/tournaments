package de.bytemc.tournaments.lobby.protocol.lobby

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInSendToTournamentLobby : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        buffer.release()
        return unit()
    }
}
