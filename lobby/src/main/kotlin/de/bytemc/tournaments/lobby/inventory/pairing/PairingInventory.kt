package de.bytemc.tournaments.lobby.inventory.pairing

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.core.entitiesutils.inventories.ClickResult
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.core.playerutils.BytePlayer
import de.bytemc.tournaments.lobby.LobbyTournament
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayerManager
import eu.thesimplecloud.api.service.ICloudService
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Nico_ND1
 */
class PairingInventory(val tournament: LobbyTournament, bytePlayer: BytePlayer) : ClickInventory(6 * 9, "Paarungen") {
    private val bytePlayer: BytePlayer
    private val itemsArray: Array<Array<ItemStack?>>?
    private val slots: Array<Array<PairingSlot?>>?
    protected var startingX = 0
    protected var startingZ = 0

    init {
        this.bytePlayer = bytePlayer
        val pairings = tournament.pairings
        if (pairings != null) {
            slots = pairings.slots
            itemsArray = Array(slots.size) { arrayOfNulls(slots[0].size) }

            for (i in slots.indices) {
                val innerSlots: Array<PairingSlot?> = slots[i]
                for (i1 in innerSlots.indices) {
                    val slot = innerSlots[i1]
                    if (slot != null) {
                        itemsArray[i][i1] = getItem(slot)
                    }
                }
            }
        } else {
            itemsArray = Array(6) { arrayOfNulls(9) }
            slots = Array(6) { arrayOfNulls(9) }
        }
        setItems()
        setNavigatingItems()
    }

    override fun onClose(player: Player) {
        for (slot in DIRECTION_SLOTS) {
            player.inventory.setItem(slot, null)
        }
        player.inventory.setItem(22, null)
    }

    private fun getItem(slot: PairingSlot): ItemStack {
        var itemCreator = ItemCreator(ItemStack(Material.INK_SACK, 1, 5.toShort()))
        if (slot.slotType == PairingSlot.SlotType.FINAL) {
            itemCreator = ItemCreator(ItemStack(Material.INK_SACK, 1, 10.toShort()))
        } else if (slot.info.round == tournament.currentRound()!!.count) {
            val encounter = slot.info.encounter
            if (encounter != null) {
                if (encounter.firstTeam.participants.any { par -> par.uuid == bytePlayer.uuid } || encounter.secondTeam.participants.any { par -> par.uuid == bytePlayer.uuid }) {
                    itemCreator = ItemCreator(ItemStack(Material.INK_SACK, 1, 9.toShort()))
                }
            }
        } else {
            itemCreator = ItemCreator(ItemStack(Material.INK_SACK, 1, 8.toShort()))
        }
        val info: PairingInfo = slot.info
        return itemCreator.setName(info.pairingDisplay).setLore(*info.pairingLore).toItemStack()
    }

    private fun setItems() {
        clear()
        for (x in startingX until startingX + 6) {
            if (x >= itemsArray!!.size) {
                continue
            }
            for (z in startingZ until startingZ + 9) {
                if (z >= itemsArray[x].size) {
                    continue
                }
                val pairingSlot = slots!![x - startingX][z - startingZ]
                setItem((x - startingX) * 9 + (z - startingZ), object : ClickableItem(itemsArray[x][z]) {
                    override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                        if (pairingSlot == null) {
                            return ClickResult.CANCEL
                        }
                        val encounter = pairingSlot.info.encounter ?: return ClickResult.CANCEL
                        val serviceID = encounter.serviceID ?: return ClickResult.CANCEL
                        for (cloudService in CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()) {
                            if (serviceID == cloudService.getUniqueId()) {
                                connect(player, cloudService)
                            }
                        }
                        return ClickResult.CANCEL
                    }
                })
            }
        }
        Bukkit.getPlayer(bytePlayer.uuid).updateInventory()
    }

    private fun connect(player: Player, cloudService: ICloudService) {
        val playerManager: ICloudPlayerManager = CloudAPI.instance.getCloudPlayerManager()
        val cloudPlayer = playerManager.getCachedCloudPlayer(player.uniqueId)
        cloudPlayer?.connect(cloudService)
    }

    private fun setNavigatingItems() {
        val player: Player = Bukkit.getPlayer(bytePlayer.uuid)
        for (i in DIRECTION_SLOTS.indices) {
            val slot = DIRECTION_SLOTS[i]
            player.inventory.setItem(slot, DIRECTION_ITEMS[i])
        }
        player.inventory.setItem(22, BACK_ITEM_STACK)
    }

    fun navigateUp() {
        if (startingX + 7 < itemsArray!!.size) {
            startingX++
            setItems()
        }
    }

    fun navigateRight() {
        if (startingZ + 10 < itemsArray!![0].size) {
            startingZ++
            setItems()
        }
    }

    fun navigateDown() {
        if (startingX - 1 >= 0) {
            startingX--
            setItems()
        }
    }

    fun navigateLeft() {
        if (startingZ - 1 >= 0) {
            startingZ--
            setItems()
        }
    }

    companion object {
        protected val DIRECTION_ITEMS = arrayOf<ItemStack>(
            ItemCreator("6ccbf9883dd359fdf2385c90a459d737765382ec4117b04895ac4dc4b60fc").setName("§8» §6Hoch")
                .toItemStack(),
            ItemCreator("682ad1b9cb4dd21259c0d75aa315ff389c3cef752be3949338164bac84a96e").setName("§8» §6Rechts")
                .toItemStack(),
            ItemCreator("72431911f4178b4d2b413aa7f5c78ae4447fe9246943c31df31163c0e043e0d6").setName("§8» §6Runter")
                .toItemStack(),
            ItemCreator("37aee9a75bf0df7897183015cca0b2a7d755c63388ff01752d5f4419fc645").setName("§8» §6Links")
                .toItemStack()
        )
        val DIRECTION_SLOTS = intArrayOf(13, 23, 31, 21)
        private val BACK_ITEM_STACK: ItemStack =
            ItemCreator("1c27235de3a55466b627459f1233596ab6a22c435cfc89a4454b47d32b199431")
                .setName("§8» §6Zurück").toItemStack()
    }
}
