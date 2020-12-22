package de.bytemc.tournaments.common.protocol.state

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutSetState : BytePacket {

    constructor(tournament: ITournament) {
        writeUUID(tournament.id())
        buffer.writeInt(tournament.state().ordinal)
    }

    constructor()

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }

}
