package de.bytemc.tournaments.server.protocol.state

import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.readUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketInSetState : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val id = readUUID()
        val state = TournamentState.values()[buffer.readInt()]

        return success(findAndUpdate(id, state))
    }

    private fun findAndUpdate(id: UUID, state: TournamentState): Boolean {
        val tournament = ServerTournamentAPI.instance.findTournament(id)
        return tournament?.setState(state) ?: false
    }
}
