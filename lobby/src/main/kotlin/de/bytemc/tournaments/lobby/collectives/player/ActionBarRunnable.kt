package de.bytemc.tournaments.lobby.collectives.player

import de.bytemc.tournaments.lobby.collectives.ICollectivesRepository
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer

/**
 * @author Nico_ND1
 */
class ActionBarRunnable(private val repository: ICollectivesRepository) : Runnable {
    override fun run() {
        for (player in repository.players()) {
            val tournament = player.tournament
            val team = player.getTeam()

            val bukkitPlayer = player.player
            val text = "§7Ersteller: §e${tournament.creator().name} §8| §7Dein Team: §b${team.name()}"
            val component = IChatBaseComponent.ChatSerializer.a("{\"text\":\"$text\"}")
            val packet = PacketPlayOutChat(component, 2)

            (bukkitPlayer as CraftPlayer).handle.playerConnection.sendPacket(packet)
        }
    }
}
