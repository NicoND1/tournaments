package de.bytemc.tournaments.server.protocol

import de.bytemc.tournaments.server.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketOutDeleteTournament(tournamentID: UUID) : BytePacket() {

    init {
        writeUUID(tournamentID)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }
}
