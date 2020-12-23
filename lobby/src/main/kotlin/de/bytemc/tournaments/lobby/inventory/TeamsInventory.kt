package de.bytemc.tournaments.lobby.inventory

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.BooleanResult
import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.common.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.common.protocol.team.PacketOutRemoveTeamParticipant
import de.bytemc.tournaments.lobby.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.ChatPaginator
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * @author Nico_ND1
 */
class TeamsInventory(
    player: Player,
    private val tournament: LobbyTournament,
) : DefaultPageableInventory<TournamentTeam>(player,
    ArrayList(tournament.teams()),
    4 * 9,
    "Teams",
    34,
    33,
    *IntStream.rangeClosed(9, 26).toArray()), ITournamentInventory {

    init {
        design(player, 31, 0, 3)
        init()
    }

    private var participationCooldown: Long = 0L

    override fun onClick(p0: Player, p1: TournamentTeam) {
        if (participationCooldown > System.currentTimeMillis()) {
            player.sendMessage("COOLDOWN JUNGE")
            player.playSound(player.location, Sound.VILLAGER_NO, 1f, 1f)
            return
        }
        participationCooldown = System.currentTimeMillis() + ManageInventory.PARTICIPATION_COOLDOWN

        val team = tournament.findTeam(player.uniqueId)
        val packet = if (team != null && team.id == p1.id) {
            PacketOutAddTeamParticipant(tournament, p1, player.toParticipant())
        } else {
            PacketOutRemoveTeamParticipant(tournament, p1, player.toParticipant())
        }

        LobbyTournamentAPI.instance.sendPacket(packet, BooleanResult::class.java).addResultListener {
            if (it.result) {
                drawPage()
                player.updateInventory()
            } else {
                player.playSound(player.location, Sound.VILLAGER_NO, 1f, 1f)
            }
        }.throwFailure()
    }

    override fun getItemStack(p0: TournamentTeam): ItemStack {
        val item = if (tournament.settings().teamsOption.playersPerTeam == 1) {
            if (p0.isEmpty()) {
                getNumberItem(0)
            } else {
                getNumberItem(NUMBERS.size)
            }
        } else {
            getNumberItem(p0.participants.size)
        }

        item.setName(player.format("Team ${player.primaryColor()}#${p0.id}"))

        val lore = "§7Spieler: ${p0.participants.stream().map { par -> par.name }.collect(Collectors.joining(", "))}"
        item.setLore(*ChatPaginator.wordWrap(lore, 40))

        val itemStack = item.toItemStack().clone()
        if (tournament.settings().teamsOption.playersPerTeam == 1 && !p0.isEmpty()) {
            val participant = p0.participants[0]
            if (participant.texture != null) setSkullOwner(itemStack, participant)
        }

        return itemStack
    }

    private fun setSkullOwner(itemStack: ItemStack, participant: TournamentParticipant) {
        val texture = participant.texture!!
        val property = Property(participant.name, texture.value, texture.signature)
        val profile = GameProfile(participant.uuid, participant.name)
        profile.properties.put("textures", property)

        val itemMeta = itemStack.itemMeta
        val field = itemMeta.javaClass.getDeclaredField("profile")
        field.isAccessible = true
        field.set(itemMeta, profile)

        itemStack.itemMeta = itemMeta
    }

    companion object {
        private val NUMBERS = arrayOf(
            ItemCreator("0ebe7e5215169a699acc6cefa7b73fdb108db87bb6dae2849fbe24714b27"),     // 0
            ItemCreator("71bc2bcfb2bd3759e6b1e86fc7a79585e1127dd357fc202893f9de241bc9e530"), // 1
            ItemCreator("4cd9eeee883468881d83848a46bf3012485c23f75753b8fbe8487341419847"),   // 2
            ItemCreator("1d4eae13933860a6df5e8e955693b95a8c3b15c36b8b587532ac0996bc37e5"),   // 3
            ItemCreator("d2e78fb22424232dc27b81fbcb47fd24c1acf76098753f2d9c28598287db5")     // 4
        )
    }

    private fun getNumberItem(number: Int): ItemCreator {
        return if (number >= NUMBERS.size) {
            ItemCreator(Material.SKULL_ITEM, 1, 3)
        } else {
            NUMBERS[number]
        }
    }

    override fun getTournament(): ITournament {
        return tournament
    }

}
