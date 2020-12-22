package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.api.PlayerTexture
import de.bytemc.tournaments.api.TournamentParticipant
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * @author Nico_ND1
 */
fun Player.toParticipant(): TournamentParticipant {
    this as CraftPlayer
    val property = profile.properties["textures"].firstOrNull() ?: return TournamentParticipant(uniqueId, name)
    return TournamentParticipant(uniqueId, name, PlayerTexture(property.value, property.signature))
}
