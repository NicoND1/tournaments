package de.bytemc.tournaments.server.protocol.round

import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutStartRound(tournament: ServerTournament, round: TournamentRound) : BytePacket() {

    init {
        writeUUID(tournament.id())
        buffer.writeInt(round.count)
        buffer.writeInt(round.encounters.size)
        for (encounter in round.encounters) {
            buffer.writeInt(encounter.id)
            buffer.writeInt(encounter.firstTeam.id)
            buffer.writeInt(encounter.secondTeam.id)
        }
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }

}
