package de.bytemc.tournaments.lobby.inventory.create

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.core.entitiesutils.inventories.ClickResult
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.lobby.format
import de.bytemc.tournaments.lobby.inventory.design
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.ln

/**
 * @author Nico_ND1
 */
class SelectTeamsAmountInventory(
    val player: Player, val context: CreationContext.Builder,
) : ClickInventory(3 * 9, player.format("§lTurnier §8- §7Teams")) {

    private var amount = AtomicInteger(MIN_TEAMS)

    init {
        design(player, 22, 0, 2)
        setItems()
    }

    private fun setItems() {
        setAddingItem(12, false, 2)
        setAddingItem(14, true, 2)

        setAmountItem()
    }

    private fun setAmountItem() {
        val rounds = Math.log(amount.get().toDouble()) / LOG_OF_TWO
        val isFair = rounds.toInt() == rounds.toDouble().toInt()

        val lore: ArrayList<String> = arrayListOf()
        lore.add("§7Klicke hier, um fortzufahren")
        if (!isFair) {
            lore.add(" ")
            lore.add("§cEs wird leere/instant win Runden geben,")
            lore.add("§cweil die Anzahl der Teams")
            lore.add("§cnicht einem normalen Turnier entspricht")
            lore.add("§7(z.B. 8, 16, 32, ...)")
        }

        setItem(13, object : ClickableItem(VALUE_ITEM
            .setName(player.format("Teams§8: §7${amount.get()}")).setLore(lore)
            .toItemStack()) {
            override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                context.teamsAmount(amount.get())
                return ClickResult.CANCEL
            }
        })
    }

    private fun setAddingItem(slot: Int, increment: Boolean, step: Int) {
        val item = ItemCreator(if (increment) INCREMENT_ITEM else DECREMENT_ITEM)
            .setName(getName(increment, step))
            .toItemStack()

        setItem(slot, AddingClickableItem(this, item, amount, increment, step))
    }

    private fun getName(increment: Boolean, step: Int): String {
        return if (increment) {
            if (amount + step <= MAX_TEAMS) "§a+${step}" else "§c+${step}"
        } else {
            if (amount - step >= MIN_TEAMS) "§a-${step}" else "§c-${step}"
        }
    }

    private class AddingClickableItem(
        val inventory: SelectTeamsAmountInventory,
        itemStack: ItemStack,
        val amount: AtomicInteger,
        val increment: Boolean,
        val step: Int,
    ) :
        ClickableItem(itemStack) {
        override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
            val amount = this.amount.get()

            if (increment) {
                if (amount + step <= MAX_TEAMS) {
                    this.amount.addAndGet(step)
                }
            } else {
                if (amount - step >= MIN_TEAMS) {
                    this.amount.addAndGet(-step)
                }
            }

            inventory.setItems()
            player.updateInventory()
            return ClickResult.CANCEL
        }
    }

    companion object {

        private val INCREMENT_ITEM = ItemStack(Material.WOOD_BUTTON)
        private val DECREMENT_ITEM = ItemStack(Material.WOOD_BUTTON)
        private val VALUE_ITEM = ItemCreator("5a6787ba32564e7c2f3a0ce64498ecbb23b89845e5a66b5cec7736f729ed37")
        private const val MIN_TEAMS = 8
        private const val MAX_TEAMS = 64
        private val LOG_OF_TWO = ln(2.0)
    }

}
