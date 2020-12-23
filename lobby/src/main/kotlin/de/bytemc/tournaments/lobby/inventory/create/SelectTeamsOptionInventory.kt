package de.bytemc.tournaments.lobby.inventory.create

import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.TournamentTeamsOption
import de.bytemc.tournaments.lobby.format
import de.bytemc.tournaments.lobby.inventory.DefaultPageableInventory
import de.bytemc.tournaments.lobby.inventory.design
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class SelectTeamsOptionInventory(
    player: Player,
    private val context: CreationContext.Builder,
) : DefaultPageableInventory<TournamentTeamsOption>(player,
    context.game!!.teamsOptions,
    3 * 9,
    "§lTurnier §8- §7Teamgröße",
    25, 24,
    *IntStream.rangeClosed(10, 16).toArray()) {

    init {
        design(player, 22, 0, 2)
        init()
    }

    override fun onClick(p0: Player, p1: TournamentTeamsOption) {
        SelectTeamsAmountInventory(player, context.teamsOption(p1)).open(player)
    }

    override fun getItemStack(p0: TournamentTeamsOption): ItemStack {
        return ItemCreator(Material.valueOf(context.game!!.materialName))
            .setName(player.format("Variante §8- §7( §e2x${p0.playersPerTeam} §7)"))
            .toItemStack()
    }

}
