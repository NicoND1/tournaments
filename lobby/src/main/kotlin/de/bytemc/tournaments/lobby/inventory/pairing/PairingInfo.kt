package de.bytemc.tournaments.lobby.inventory.pairing

import de.bytemc.tournaments.api.TournamentEncounter

/**
 * @author Nico_ND1
 */
data class PairingInfo @JvmOverloads constructor(
    val round: Int,
    val pairingDisplay: String,
    val pairingLore: Array<String?>,
    val encounter: TournamentEncounter? = null,
)
