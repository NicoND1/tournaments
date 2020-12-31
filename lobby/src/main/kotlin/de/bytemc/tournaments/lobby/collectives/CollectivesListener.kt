package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * @author Nico_ND1
 */
class CollectivesListener(private val repository: ICollectivesRepository) : Listener {

    @EventHandler(priority = EventPriority.HIGH)
    fun handleJoin(event: PlayerJoinEvent) {
        val player = event.player
        event.joinMessage = null

        for (tournament in LobbyTournamentAPI.instance.tournaments()) {
            for (team in tournament.teams()) {
                if (team.participants.any { it.uuid == player.uniqueId }) {
                    val collPlayer = CollectivesPlayer(player, tournament)
                    repository.addPlayer(collPlayer)
                    collPlayer.update()
                    return
                }
            }
        }

        player.kickPlayer("Du bist in keinem Turnier")
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
        }
    }

}
