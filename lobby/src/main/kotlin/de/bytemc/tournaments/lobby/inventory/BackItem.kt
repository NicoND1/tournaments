package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.core.entitiesutils.inventories.ClickResult
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.lobby.secondaryColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Nico_ND1
 */
class BackItem(player: Player) : ClickableItem(
    ItemCreator("bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9")
        .setName("§8» ${player.secondaryColor()}§lZurück")
        .setLore("§7Klicke hier, um zurück zu gehen")
        .toItemStack()
) {
    override fun onClick(p0: Player?, p1: ItemStack?): ClickResult {
        ClickInventory.getClickInventory(p0!!.uniqueId).ifPresent { it.parent.openSilent(p0) }
        return ClickResult.CANCEL
    }
}
