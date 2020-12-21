package de.bytemc.tournaments.server.protocol

import de.bytemc.tournaments.api.ITournament
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutCreateTournament : JsonPacket {

    constructor(tournament: ITournament) {
        jsonLib.append("id", tournament.id())
        jsonLib.append("state", tournament.state())
        jsonLib.append("creator", tournament.creator())
        jsonLib.append("settings", tournament.settings())
        jsonLib.append("teams", tournament.teams())
    }

    constructor()

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }
}
