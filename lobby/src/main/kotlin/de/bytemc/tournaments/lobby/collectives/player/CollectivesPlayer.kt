package de.bytemc.tournaments.lobby.collectives.player

import de.bytemc.core.ByteAPI
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.TournamentLobbyPlugin
import de.bytemc.tournaments.lobby.collectives.CollectivesScoreboard
import de.bytemc.tournaments.lobby.inventory.PlayersInventory
import de.bytemc.tournaments.lobby.inventory.TeamsInventory
import eu.thesimplecloud.api.CloudAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nico_ND1
 */
data class CollectivesPlayer(val player: Player, val tournament: LobbyTournament) {

    companion object {
        const val ALL_PLAYERS_SLOT = 0
        const val ALL_TEAMS_SLOT = 1
        const val BACK_TO_LOBBY_SLOT = 8
    }

    val bytePlayer = ByteAPI.getInstance().bytePlayerManager.players[player.uniqueId]!!
    private val scoreboard = CollectivesScoreboard(this)

    fun update() {
        updateScoreboard()

        setAllPlayers()
        setAllTeams()
        setBackToLobby()

        hideOthers()
    }

    fun updateScoreboard() {
        scoreboard.update()
    }

    private fun hideOthers() {
        val repository = JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java).collectives.repository()
        for (player in repository.players()) {
            val bukkitPlayer = player.player

            if (player.tournament != tournament) {
                bukkitPlayer.hidePlayer(this.player)
                this.player.hidePlayer(bukkitPlayer)
            }
        }
    }

    private fun setAllPlayers() {
        player.inventory.setItem(ALL_PLAYERS_SLOT,
            ItemCreator(Material.SKULL_ITEM, (3).toShort()).setName("${color()}Alle Spieler").toItemStack())
    }

    fun allPlayers() {
        PlayersInventory(player, getTeam(), tournament).open(player)
    }

    private fun setAllTeams() {
        if (tournament.settings().teamsOption.playersPerTeam > 1) {
            player.inventory.setItem(ALL_TEAMS_SLOT,
                ItemCreator(Material.BED).setName("${color()}Alle Teams").toItemStack())
        }
    }

    fun allTeams() {
        TeamsInventory(player, tournament).open(player)
    }

    private fun setBackToLobby() {
        player.inventory.setItem(BACK_TO_LOBBY_SLOT,
            ItemCreator(Material.SLIME_BALL).setName("${color()}Lobby").toItemStack())
    }

    fun backToLobby() {
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(player.uniqueId) ?: return
        CloudAPI.instance.getCloudPlayerManager().sendPlayerToLobby(cloudPlayer)
    }

    fun color(): String = bytePlayer.secondColor

    fun getTeam(): TournamentTeam {
        return tournament.teams().first { it -> it.participants.any { it.uuid == player.uniqueId } }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as CollectivesPlayer
        if (player != other.player) return false
        return true
    }

    override fun hashCode(): Int {
        return player.hashCode()
    }
}
