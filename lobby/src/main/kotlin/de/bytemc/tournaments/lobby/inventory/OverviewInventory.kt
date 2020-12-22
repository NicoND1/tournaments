package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.format
import de.bytemc.tournaments.lobby.primaryColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class OverviewInventory(
    player: Player,
) : DefaultPageableInventory<LobbyTournament>(player,
    ArrayList(LobbyTournamentAPI.instance.tournaments()),
    3 * 9, "Turniere", 25, 24,
    *IntStream.rangeClosed(9, 17).toArray()
) {

    init {
        design(player, 22, 0, 2)
        init()
    }

    override fun onClick(p0: Player, p1: LobbyTournament) {
        ManageInventory(p0, p1).open(p0)
    }

    override fun getItemStack(tournament: LobbyTournament?): ItemStack {
        val game = tournament!!.settings().game
        val creator = tournament.creator().name
        var playerCount = 0
        for (team in tournament.teams()) {
            playerCount += team.participants.size
        }

        return ItemCreator(Material.valueOf(game.materialName))
            .setName(player.format("Turnier von ${player.primaryColor()}$creator"))
            .setLore(arrayListOf(
                "§7Spielmodus: ${game.color}${game.name}",
                "§7Teilnehmer: ${game.color}${playerCount}"
            )).toItemStack()
    }

}