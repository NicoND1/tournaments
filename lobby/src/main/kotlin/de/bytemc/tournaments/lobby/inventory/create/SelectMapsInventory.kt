package de.bytemc.tournaments.lobby.inventory.create

import de.bytemc.core.entitiesutils.inventories.ClickResult
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.TournamentCreator
import de.bytemc.tournaments.api.TournamentMap
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.format
import de.bytemc.tournaments.lobby.inventory.DefaultPageableInventory
import de.bytemc.tournaments.lobby.inventory.design
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class SelectMapsInventory(
    player: Player,
    private val context: CreationContext.Builder,
) : DefaultPageableInventory<TournamentMap>(player,
    context.teamsOption!!.mapPool,
    4 * 9,
    "§lTurnier §8- §7Maps",
    34, 33,
    *IntStream.rangeClosed(9, 26).toArray()) {

    init {
        setItem(31, object : ClickableItem(ItemCreator(Material.INK_SACK, 10)
            .setName(player.format("§lTurnier erstellen"))
            .toItemStack()) {
            override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                createTournament()
                player.closeInventory()
                return ClickResult.CANCEL
            }
        })

        design(player, 31, 0, 3)
        init()
    }

    private fun createTournament() {
        val creator = TournamentCreator(player.uniqueId, player.name)
        LobbyTournamentAPI.instance.createTournament(creator, context.build()).addResultListener {
            if (it) {
                val components = ComponentBuilder("Turnier wurde erstellt.\n ")
                    .append("[Verwalten]").event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament manage"))
                    .create()
                player.spigot().sendMessage(*components)
            } else {
                player.sendMessage("Turnier konnte nicht erstellt werden")
            }
        }
    }

    override fun onClick(player: Player, map: TournamentMap) {
        if (context.maps.contains(map)) {
            context.removeMap(map)
        } else {
            context.addMap(map)
        }

        drawPage()
        player.updateInventory()
    }

    override fun getItemStack(map: TournamentMap): ItemStack {
        val material = if (context.maps.contains(map)) Material.MAP else Material.EMPTY_MAP
        return ItemCreator(material).setName(player.format(map.name)).toItemStack()
    }

}
