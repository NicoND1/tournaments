package de.bytemc.tournaments.lobby.collectives.armorstand

import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.lobby.collectives.ICollectivesRepository
import de.bytemc.tournaments.lobby.inventory.setSkullOwner
import net.codergames.responsive.hologram.Hologram
import net.codergames.responsive.hologram.interaction.Interactable
import net.codergames.responsive.hologram.interaction.InteractionEvent
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

/**
 * @author Nico_ND1
 */
class TopListHologram(val repository: ICollectivesRepository) : Hologram(0), Interactable {

    override fun getText(player: Player): String {
        val emptyText = "§6Platz 1: §c/"
        val collectivesPlayer = repository.findPlayer(player) ?: return emptyText
        val tournament = collectivesPlayer.tournament

        if (tournament.state() == TournamentState.FINISHED) {
            val lastRound = tournament.currentRound() ?: return emptyText
            val winner = lastRound.encounters[0].winnerTeam ?: return emptyText

            return "§6Platz 1: §l${winner.name()}"
        } else {
            return emptyText
        }
    }

    override fun getEquipment(player: Player, slot: EquipmentSlot): ItemStack {
        return when (slot) {
            EquipmentSlot.HEAD -> {
                val collectivesPlayer = repository.findPlayer(player) ?: return super.getEquipment(player, slot)
                val tournament = collectivesPlayer.tournament

                if (tournament.state() == TournamentState.FINISHED) {
                    val lastRound = tournament.currentRound() ?: return super.getEquipment(player, slot)
                    val winner = lastRound.encounters[0].winnerTeam ?: return super.getEquipment(player, slot)

                    if (winner.participants.isEmpty()) return super.getEquipment(player, slot)
                    val participant = winner.participants[0]
                    val skull = ItemStack(Material.SKULL_ITEM, 1, 3)
                    participant.setSkullOwner(skull)

                    skull
                } else {
                    super.getEquipment(player, slot)
                }
            }
            EquipmentSlot.CHEST -> ItemStack(Material.GOLD_CHESTPLATE)
            EquipmentSlot.FEET -> ItemStack(Material.GOLD_BOOTS)
            EquipmentSlot.LEGS -> ItemStack(Material.GOLD_LEGGINGS)
            else -> return super.getEquipment(player, slot)
        }
    }

    override fun onInteract(p0: InteractionEvent?) {
    }
}
