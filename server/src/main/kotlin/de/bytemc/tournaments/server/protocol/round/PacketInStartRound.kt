package de.bytemc.tournaments.server.protocol.round

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInStartRound : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
