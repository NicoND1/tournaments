package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.entity.Player

/**
 * @author Nico_ND1
 */
interface ICollectivesRepository {

    fun players(): List<CollectivesPlayer>

    fun addPlayer(collectivesPlayer: CollectivesPlayer)

    fun removePlayer(collectivesPlayer: CollectivesPlayer)

    fun findPlayer(player: Player): CollectivesPlayer?

}
