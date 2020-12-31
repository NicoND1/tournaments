package de.bytemc.tournaments.lobby.inventory

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.inventories.NoneClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.TournamentParticipant
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class TournamentInventory {

    companion object {
        private val GLASS: ItemStack = ItemCreator(Material.STAINED_GLASS_PANE, 7.toShort()).setName(" ").toItemStack()
        val CLICKABLE_GLASS: ClickableItem = NoneClickableItem(GLASS)
    }

}

fun ClickInventory.fillWithGlassArray(vararg ranges: Int) {
    for (i in ranges) {
        setItemIfAbsent(i, TournamentInventory.CLICKABLE_GLASS)
    }
}

fun ClickInventory.fillWithGlass(from: Int, to: Int) {
    fillWithGlass(*IntStream.range(from, to).toArray())
}

fun ClickInventory.fillWithGlass(vararg rows: Int) {
    val array = IntArray(rows.size * 9)
    var count = 0
    for (row in rows) {
        for (i1 in 0..8) {
            array[count++] = row * 9 + i1
        }
    }
    fillWithGlassArray(*array)
}

fun ClickInventory.design(player: Player, backItemSlot: Int, vararg rows: Int) {
    fillWithGlass(*rows)
    if (backItemSlot != -1) {
        setItem(backItemSlot, BackItem(player))
    }
}

fun TournamentParticipant.setSkullOwner(itemStack: ItemStack) {
    val texture = texture!!
    val property = Property(name, texture.value, texture.signature)
    val profile = GameProfile(uuid, name)
    profile.properties.put("textures", property)

    val itemMeta = itemStack.itemMeta
    val field = itemMeta.javaClass.getDeclaredField("profile")
    field.isAccessible = true
    field.set(itemMeta, profile)

    itemStack.itemMeta = itemMeta
}
