package de.bytemc.tournaments.common.protocol

import de.bytemc.tournaments.api.*
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketOutCreateTournament : BytePacket {

    constructor(tournament: ITournament) {
        init(tournament.id(), tournament.creator(), tournament.settings())
    }

    constructor(id: UUID, creator: TournamentCreator, settings: TournamentSettings) {
        init(id, creator, settings)
    }

    private fun init(
        id: UUID,
        creator: TournamentCreator,
        settings: TournamentSettings,
    ) {
        writeUUID(id)
        writeUUID(creator.uuid)
        writeString(creator.name)
        writeString(settings.game.name)
        buffer.writeInt(settings.teamsOption.playersPerTeam)
        buffer.writeInt(settings.teamsAmount)
        writeMaps(settings.maps)
    }

    private fun writeMaps(maps: List<TournamentMap>) {
        buffer.writeInt(maps.size)
        for (map in maps) {
            writeString(map.name)
        }
    }

    constructor()

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
