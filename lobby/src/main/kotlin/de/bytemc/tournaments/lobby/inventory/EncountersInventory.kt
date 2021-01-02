package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.TournamentEncounter
import eu.thesimplecloud.api.CloudAPI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.ChatPaginator
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class EncountersInventory(
    player: Player,
    collection: MutableList<TournamentEncounter>?,
) : DefaultPageableInventory<TournamentEncounter>(player,
    collection?.filter { !it.secondTeam.isEmpty() && !it.firstTeam.isEmpty() }?.toCollection(ArrayList()),
    3 * 9,
    "Runden",
    25,
    24,
    *IntStream.rangeClosed(9, 17).toArray()) {

    init {
        design(player, -1, 0, 2)
        init()
    }

    override fun onClick(p0: Player, encounter: TournamentEncounter) {
        if (encounter.serviceID != null) {
            val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(player.uniqueId) ?: return
            val cloudService = CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()
                .firstOrNull { it.getUniqueId() == encounter.serviceID } ?: return
            cloudPlayer.connect(cloudService)
        }
    }

    override fun getItemStack(encounter: TournamentEncounter): ItemStack {
        val firstNames = ChatPaginator.wordWrap("§7" + encounter.firstTeam.name(), 35)
        val secondNames = ChatPaginator.wordWrap("§7" + encounter.secondTeam.name(), 35)
        val lore: ArrayList<String> = ArrayList(firstNames.size + secondNames.size + 1)
        lore.addAll(firstNames).let { lore.add(" ") }.let { lore.addAll(secondNames) }

        return ItemCreator(Material.NETHER_STAR).setName("§e#${encounter.firstTeam.id} §7VS §e#${encounter.secondTeam.id}")
            .setLore(lore)
            .toItemStack()
    }
}
