package de.bytemc.tournaments.game

import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nico_ND1
 */
class ShutdownTimeStep(duration: Long) : TimeStep(duration) {
    override fun trigger() {
        val plugin = JavaPlugin.getPlugin(TournamentGamePlugin::class.java)
        val matchData = plugin.getMatchData() ?: return
        val onlinePlayers = Bukkit.getOnlinePlayers().map { it.uniqueId }

        var shutdownDelay = 3 * 20
        when {
            matchData.firstTeam.participants.any { onlinePlayers.contains(it.uuid) } -> {
                plugin.finish(matchData.firstTeam)
                Bukkit.broadcastMessage("Das Team ${matchData.firstTeam.name()} hat automatisch gewonnen")
            }
            matchData.secondTeam.participants.any { onlinePlayers.contains(it.uuid) } -> {
                plugin.finish(matchData.secondTeam)
                Bukkit.broadcastMessage("Das Team ${matchData.secondTeam.name()} hat automatisch gewonnen")
            }
            else -> {
                Bukkit.broadcastMessage("Dieser Server konnte keinen Gewinner feststellen, der Server wird sich einen selbst aussuchen")
                shutdownDelay = 10
            }
        }

        shutdownAfter(plugin, shutdownDelay.toLong())
    }

    private fun shutdownAfter(plugin: Plugin, ticks: Long) {
        Bukkit.getScheduler().runTaskLater(plugin, {
            Bukkit.getScheduler().runTaskLater(plugin, {
                Bukkit.shutdown()
            }, 30)

            for (onlinePlayer in Bukkit.getOnlinePlayers()) {
                onlinePlayer.kickPlayer("")
            }
        }, ticks)
    }
}
