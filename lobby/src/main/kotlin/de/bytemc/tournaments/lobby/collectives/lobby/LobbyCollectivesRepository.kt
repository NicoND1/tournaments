package de.bytemc.tournaments.lobby.collectives.lobby

import de.bytemc.tournaments.lobby.collectives.ICollectivesRepository
import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Nico_ND1
 */
class LobbyCollectivesRepository : ICollectivesRepository {
    override fun players(): List<CollectivesPlayer> = Collections.emptyList()

    override fun addPlayer(collectivesPlayer: CollectivesPlayer) {
    }

    override fun removePlayer(collectivesPlayer: CollectivesPlayer) {
    }

    override fun findPlayer(player: Player): Nothing? = null
}
