package de.bytemc.tournaments.lobby.protocol.state

import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
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
        val tournament = LobbyTournamentAPI.instance.findTournament(id)
        if (tournament != null) {
            tournament.currentState = state
            return true
        }
        return false
    }

}
