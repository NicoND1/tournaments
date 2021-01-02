package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class CollectivesPlayerRepository : ICollectivesRepository {

    private val players = ArrayList<CollectivesPlayer>()
    private val lock = ReentrantLock()

    override fun players() = ArrayList(players)

    override fun addPlayer(collectivesPlayer: CollectivesPlayer) {
        lock.withLock { players.add(collectivesPlayer) }
    }

    override fun removePlayer(collectivesPlayer: CollectivesPlayer) {
        lock.withLock { players.remove(collectivesPlayer) }
    }

    override fun findPlayer(player: Player): CollectivesPlayer? {
        return players.firstOrNull { it.player == player }
    }

    override fun findPlayer(uuid: UUID): CollectivesPlayer? {
        return players.firstOrNull { it.player.uniqueId == uuid }
    }

}
