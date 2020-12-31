package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.TournamentLobbyPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

/**
 * @author Nico_ND1
 */
class CollectivesImpl(javaPlugin: JavaPlugin, private val repository: ICollectivesRepository) : ICollectives {

    val config: CollectivesConfig

    init {
        javaPlugin.saveDefaultConfig()

        val configuration = javaPlugin.config
        config = CollectivesConfig(configuration.getConfigurationSection("spawnLocation"))
    }

    override fun repository() = repository

    override fun handleRoundStart(tournament: LobbyTournament) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)) {
            for (player in repository.players()) {
                if (player.tournament == tournament) {
                    player.update()
                }
            }
        }
    }

    override fun handleEncounterWin(encounter: TournamentEncounter) {
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)) {
            for (player in repository.players()) {
                val team = player.getTeam()
                if (team == encounter.firstTeam || team == encounter.secondTeam) {
                    player.update()
                }
            }
        }
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
                    player.updateScoreboard()
                }
            }
        }
    }
}
