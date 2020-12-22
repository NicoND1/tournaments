package de.bytemc.tournaments.lobby.inventory.create

import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.TournamentGame
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.format
import de.bytemc.tournaments.lobby.inventory.DefaultPageableInventory
import de.bytemc.tournaments.lobby.inventory.design
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class SelectGameInventory(
    player: Player,
) : DefaultPageableInventory<TournamentGame>(player,
    ArrayList(LobbyTournamentAPI.instance.allGames()),
    3 * 9,
    "§lTurnier §8- §7Spielmodus",
    25, 24,
    *IntStream.rangeClosed(9, 17).toArray()) {

    init {
        design(player, 22, 0, 2)
        init()
    }

    override fun onClick(p0: Player, p1: TournamentGame) {
        val context = CreationContext.Builder().game(p1)
        SelectTeamsOptionInventory(p0, context).open(p0)
    }

    override fun getItemStack(p0: TournamentGame): ItemStack {
        val options = p0.teamsOptions.stream()
            .map { "2x${it.playersPerTeam}" }
            .collect(Collectors.joining("§7, §e"))

        return ItemCreator(Material.valueOf(p0.materialName))
            .setName(player.format("${p0.color}${p0.name}"))
            .setLore("§7Mögliche Teamgrößen:", "§e${options}")
            .toItemStack()
    }

}
