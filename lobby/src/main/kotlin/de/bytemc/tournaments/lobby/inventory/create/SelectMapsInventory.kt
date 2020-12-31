package de.bytemc.tournaments.lobby.inventory.create

import de.bytemc.core.ByteAPI
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
        design(player, 30, 0, 3)
        init()

        setItem(32, object : ClickableItem(ItemCreator(Material.INK_SACK, 1, 10)
            .setName(player.format("§lTurnier erstellen"))
            .toItemStack()) {
            override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                if (context.maps.isEmpty()) {
                    player.sendMessage("Mindestens eine Map du Idiot")
                    return ClickResult.CANCEL
                }

                createTournament()
                player.closeInventory()
                return ClickResult.CANCEL
            }
        })
    }

    private fun createTournament() {
        val creator = TournamentCreator(player.uniqueId, player.name)
        LobbyTournamentAPI.instance.createTournament(creator, context.build()).addResultListener {
            if (it.result) {
                val bytePlayer = ByteAPI.getInstance().bytePlayerManager.players[player.uniqueId]
                val components =
                    ComponentBuilder((bytePlayer?.getPrefix("Lobby") ?: "§8» ") + "§7Dein Turnier wurde erstellt§8.\n ")
                        .append("§8» " + (bytePlayer?.secondColor ?: "§6") + "HIER Verwalten")
                        .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tournament manage"))
                        .create()
                player.spigot().sendMessage(*components)
            } else {
                player.sendMessage("§8» §7Turnier konnte nicht erstellt werden§8.")
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
