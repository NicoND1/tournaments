package de.bytemc.tournaments.common.protocol

import de.bytemc.tournaments.api.*
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

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

    constructor(
        id: UUID,
        state: TournamentState,
        creator: TournamentCreator,
        settings: TournamentSettings,
        teams: List<TournamentTeam>,
    ) {
        jsonLib.append("id", id)
        jsonLib.append("state", state)
        jsonLib.append("creator", creator)
        jsonLib.append("settings", settings)
        jsonLib.append("teams", teams)
    }

    constructor()

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
