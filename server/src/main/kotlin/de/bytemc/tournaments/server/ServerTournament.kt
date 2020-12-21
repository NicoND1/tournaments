package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.server.broadcast.BroadcastMessage
import de.bytemc.tournaments.server.protocol.state.PacketOutSetState
import java.util.*

/**
 * @author Nico_ND1
 */
class ServerTournament(
    id: UUID,
    creator: TournamentCreator,
    settings: TournamentSettings,
    teams: List<TournamentTeam>
) : AbstractTournament(id, creator, settings, teams) {

    override fun setState(newState: TournamentState): Boolean {
        if (currentState == newState) {
            return false
        }

        currentState = newState
        sendUnitPacket(PacketOutSetState(this))
        return true
    }

    fun broadcast(message: BroadcastMessage) {
        teams().forEach { _ -> broadcast(message) }
    }

}
