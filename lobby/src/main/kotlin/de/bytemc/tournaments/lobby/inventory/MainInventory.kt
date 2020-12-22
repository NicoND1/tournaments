package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.core.entitiesutils.inventories.ClickResult
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.lobby.format
import de.bytemc.tournaments.lobby.inventory.create.SelectGameInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Nico_ND1
 */
class MainInventory(val player: Player) : ClickInventory(3 * 9, player.format("§lTurnier §8- §7Turniere")) {

    init {
        design(player, -1, 0, 2)

        setItem(12, object : ClickableItem(ItemCreator(Material.MAP)
            .setName(player.format("Turnier erstellen"))
            .toItemStack()) {
            override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                SelectGameInventory(player).open(player)
                return ClickResult.CANCEL
            }
        })

        setItem(14, object : ClickableItem(ItemCreator(Material.BOOK)
            .setName(player.format("Alle Turniere"))
            .toItemStack()) {
            override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                OverviewInventory(player).open(player)
                return ClickResult.CANCEL
            }
        })
    }

}
