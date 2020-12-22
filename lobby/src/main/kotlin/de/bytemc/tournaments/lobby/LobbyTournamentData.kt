package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.api.TournamentParticipant
import org.bukkit.entity.Player

/**
 * @author Nico_ND1
 */
fun Player.toParticipant(): TournamentParticipant {
    return TournamentParticipant(uniqueId, name)
}
