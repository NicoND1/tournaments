package de.bytemc.tournaments.server.protocol

import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInStartListening : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        ServerTournamentAPI.instance.startListening(connection)
        return unit()
    }
}
