package de.bytemc.tournaments.lobby.protocol

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInStartListening : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
