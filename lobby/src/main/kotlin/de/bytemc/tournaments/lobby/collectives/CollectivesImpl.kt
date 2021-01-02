package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.TournamentLobbyPlugin
import de.bytemc.tournaments.lobby.collectives.armorstand.TopList
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * @author Nico_ND1
 */
class CollectivesImpl(javaPlugin: JavaPlugin, private val repository: ICollectivesRepository) : ICollectives {

    val config: CollectivesConfig
    private val topList: TopList

    init {
        javaPlugin.saveDefaultConfig()

        val configuration = javaPlugin.config
        config = CollectivesConfig(configuration.getConfigurationSection("spawnLocation"))
        topList = TopList(this, configuration.getConfigurationSection("topList"))
    }

    override fun repository() = repository

    override fun handleStateUpdate(tournament: LobbyTournament) {
        topList.updateAll()
    }

    override fun handleRoundStart(tournament: LobbyTournament) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)) {
            for (player in repository.players()) {
                if (player.tournament == tournament) {
                    player.update(this)
                }
            }
        }
    }

    override fun handleEncounterWin(encounter: TournamentEncounter) {
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java), {
            for (player in repository.players()) {
                val team = player.getTeam()
                if (team == encounter.firstTeam || team == encounter.secondTeam) {
                    player.update(this)

                    val cloudPlayer = player.bytePlayer.cloudPlayer
                    val isWinner = team == encounter.winnerTeam
                    if (!isWinner) {
                        cloudPlayer.sendTitle("§4ausgeschieden", "§cDu bist aus dem Turnier ausgeschieden", 15, 50, 5)
                        player.player.playSound(player.player.location, Sound.WOLF_DEATH, 1f, 1f)
                    }
                }
            }
        }, 10)
    }

    override fun handleDelete(id: UUID) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)) {
            for (player in repository.players()) {
                if (player.tournament.id() == id) {
                    player.player.kickPlayer("Das Turnier wurde gelöscht")
                }
            }
        }
    }

    override fun handleTeamUpdate(team: TournamentTeam) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)) {
            for (player in repository.players()) {
                if (player.getTeam() == team) {
                    player.updateScoreboard(this)
                }
            }
        }
    }

}
