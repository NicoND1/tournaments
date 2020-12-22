package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.entitiesutils.inventories.pageable.PageableClickInventory
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.lobby.format
import org.bukkit.entity.Player

/**
 * @author Nico_ND1
 */
abstract class DefaultPageableInventory<T : Any>(
    val player: Player,
    collection: MutableList<T>?,
    size: Int,
    title: String?,
    nextItemSlot: Int,
    previousItemSlot: Int,
    vararg slots: Int,
) : PageableClickInventory<T>(collection,
    size,
    if (title == null) title else player.format(title),
    nextItemSlot,
    NEXT_ITEM.setName(player.format("Nächste Seite")).toItemStack(),
    previousItemSlot,
    PREVIOUS_ITEM.setName(player.format("Vorherige Seite")).toItemStack(),
    *slots) {

    companion object {
        private val NEXT_ITEM = ItemCreator("2a3b8f681daad8bf436cae8da3fe8131f62a162ab81af639c3e0644aa6abac2f")
        private val PREVIOUS_ITEM = ItemCreator("8652e2b936ca8026bd28651d7c9f2819d2e923697734d18dfdb13550f8fdad5f")
    }

    override fun eraseSwitchSlot(slot: Int) {
        setItem(slot, TournamentInventory.CLICKABLE_GLASS)
    }

}
