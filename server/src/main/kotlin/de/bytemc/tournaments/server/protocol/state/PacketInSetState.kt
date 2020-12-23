package de.bytemc.tournaments.server.protocol.state

import de.bytemc.tournaments.api.BooleanResult
import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketInSetState : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<BooleanResult> {
        val id = readUUID()
        val state = TournamentState.values()[buffer.readInt()]

        return success(findAndUpdate(id, state))
    }

    private fun findAndUpdate(id: UUID, state: TournamentState): BooleanResult {
        val tournament = ServerTournamentAPI.instance.findTournament(id)
        return BooleanResult.from(tournament?.setState(state) ?: false)
    }

}
