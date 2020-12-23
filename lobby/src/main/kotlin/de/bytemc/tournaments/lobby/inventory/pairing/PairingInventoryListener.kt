package de.bytemc.tournaments.lobby.inventory.pairing

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

/**
 * @author Nico_ND1
 */
class PairingInventoryListener : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null || event.clickedInventory.type != InventoryType.PLAYER) {
            return
        }
        val player = event.whoClicked as Player
        val inventoryOptional = ClickInventory.getClickInventory(player.uniqueId)
        if (!inventoryOptional.isPresent || inventoryOptional.get() !is PairingInventory) {
            return
        }
        val inventory = inventoryOptional.get() as PairingInventory
        if (event.slot == 22) {
            ClickInventory.getClickInventory(player.uniqueId)
                .ifPresent { clickInventory: ClickInventory -> clickInventory.parent.openSilent(player) }
            return
        }
        for (i in PairingInventory.DIRECTION_SLOTS.indices) {
            val slot: Int = PairingInventory.DIRECTION_SLOTS[i]
            if (slot == event.slot) {
                when (i) {
                    0 -> inventory.navigateUp()
                    1 -> inventory.navigateRight()
                    2 -> inventory.navigateDown()
                    3 -> inventory.navigateLeft()
                }
                break
            }
        }
        event.isCancelled = true
    }
}
