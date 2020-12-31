package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class PlayersInventory(
    player: Player,
    private val ownTeam: TournamentTeam,
    private val tournament: ITournament,
) : DefaultPageableInventory<TournamentParticipant>(player,
    tournament.teams().sortedByDescending { it == ownTeam }.flatMap { it.participants }.toCollection(ArrayList()),
    4 * 9,
    "Spieler",
    34,
    33,
    *IntStream.rangeClosed(9, 26).toArray()), ITournamentInventory {

    init {
        design(player, 31, 0, 3)
        init()
    }

    override fun onClick(player: Player, participant: TournamentParticipant) {
    }

    override fun getItemStack(participant: TournamentParticipant): ItemStack {
        val color = if (ownTeam.participants.contains(participant)) "§a" else "§e"
        val item = ItemCreator(Material.SKULL_ITEM, (3).toShort()).setName("$color${participant.name}").toItemStack()
        participant.setSkullOwner(item)
        return item
    }

    override fun getTournament() = tournament
}
