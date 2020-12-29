package de.bytemc.tournaments.lobby.listener

import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author Nico_ND1
 */
class JoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        for (tournament in LobbyTournamentAPI.instance.tournaments(TournamentState.PLAYING)) {
            for (team in tournament.teams()) {
                if (team.participants.any { par -> par.uuid == player.uniqueId }) {
                    testRound(player, tournament, team)
                }
            }
        }
    }

    private fun testRound(player: Player, tournament: LobbyTournament, team: TournamentTeam) {
        val round = tournament.currentRound ?: return
        for (encounter in round.encounters) {
            if (encounter.firstTeam.id == team.id || encounter.secondTeam.id == team.id) {
                if (encounter.serviceID == null || encounter.winnerTeam != null) return

                player.spigot().sendMessage(*ComponentBuilder("Du bist eigentlich in einer Turnier Runde. Klicke ")
                    .color(ChatColor.RED)
                    .append("hier").event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament rejoin"))
                    .color(ChatColor.DARK_RED)
                    .append(" um dieser bei zu treten.").color(ChatColor.RED)
                    .create())
            }
        }
    }

}
