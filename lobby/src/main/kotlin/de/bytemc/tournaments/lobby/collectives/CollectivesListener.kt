package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.*
import org.spigotmc.event.player.PlayerSpawnLocationEvent

/**
 * @author Nico_ND1
 */
class CollectivesListener(private val collectivesImpl: CollectivesImpl) : Listener {

    private val repository = collectivesImpl.repository()

    @EventHandler
    fun handleSpawnLocation(event: PlayerSpawnLocationEvent) {
        event.spawnLocation = collectivesImpl.config.spawnLocation
    }

    @EventHandler
    fun handleRespawn(event: PlayerRespawnEvent) {
        event.respawnLocation = collectivesImpl.config.spawnLocation
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player && event.cause == EntityDamageEvent.DamageCause.VOID) {
            event.entity.teleport(collectivesImpl.config.spawnLocation)
        }
    }

    @EventHandler
    fun handleLogin(event: PlayerLoginEvent) {
        val player = event.player

        for (tournament in LobbyTournamentAPI.instance.tournaments()) {
            for (team in tournament.teams()) {
                if (team.participants.any { it.uuid == player.uniqueId }) {
                    val collPlayer = CollectivesPlayer(player, tournament, false, collectivesImpl)
                    repository.addPlayer(collPlayer)
                    return
                }
            }
        }

        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cDu bist in keinem Turnier")
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleJoin(event: PlayerJoinEvent) {
        val player = event.player
        event.joinMessage = null

        val collectivesPlayer = repository.findPlayer(player)!!
        collectivesPlayer.update(collectivesImpl)
        collectivesPlayer.join(collectivesImpl)
        collectivesPlayer.findIsPlaying()
    }

    @EventHandler
    fun handleQuit(event: PlayerQuitEvent) {
        val player = event.player
        event.quitMessage = null

        if (repository.players().isNotEmpty()) {
            repository.removePlayer(repository.findPlayer(player) ?: repository.players()[0])
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    fun handleChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        val collectivesPlayer = repository.findPlayer(player) ?: return
        val team = collectivesPlayer.getTeam()

        for (recipient in event.recipients) {
            val collectivesRecipient = repository.findPlayer(recipient) ?: continue

            if (collectivesRecipient.tournament == collectivesPlayer.tournament) {
                val recipientTeam = collectivesRecipient.getTeam()

                val color = if (recipientTeam == team) "§a" else "§e"
                val message = "§f#${team.id} $color${player.name} §8» §7${event.message}"
                recipient.sendMessage(message)
            }
        }

        event.isCancelled = true
    }

    @EventHandler
    fun handleInteract(event: PlayerInteractEvent) {
        if (event.item == null) return
        val player = event.player
        val collectivesPlayer = repository.findPlayer(player) ?: return

        when (player.inventory.heldItemSlot) {
            CollectivesPlayer.ALL_PLAYERS_SLOT -> collectivesPlayer.allPlayers()
            CollectivesPlayer.ALL_TEAMS_SLOT -> collectivesPlayer.allTeams()
            CollectivesPlayer.BACK_TO_LOBBY_SLOT -> collectivesPlayer.backToLobby()
            CollectivesPlayer.MATCHUPS_SLOT -> collectivesPlayer.matchups()
        }
    }

}
