package de.bytemc.tournaments.lobby.inventory.pairing

import com.google.common.collect.Lists
import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import java.util.stream.Collectors
import kotlin.math.ceil
import kotlin.math.ln

/**
 * @author Nico_ND1
 */
class TournamentPairings(tournament: LobbyTournament) {
    private val firstRoundMatches: Int
    val slots: Array<Array<PairingSlot?>>
    fun setSlots(round: Int, tournament: LobbyTournament) {
        val matches: Int = tournament.settings().matchCount(round)

        // Is the final match, which is displayed 4x bigger
        if (matches == 1) {
            val previousMatchCount: Int = tournament.settings().matchCount(round - 1)
            val slotDifference = (firstRoundMatches - previousMatchCount) / 2
            val slot = PairingSlot(PairingSlot.SlotType.FINAL, getPairingInfo(tournament, round, 0))
            if (previousMatchCount % 2 == 0) {
                slots[round][slotDifference] = slot
                slots[round + 1][slotDifference] = slot
            }
            slots[round][slotDifference + 1] = slot
            slots[round + 1][slotDifference + 1] = slot
            return
        }
        val slotDifference = (firstRoundMatches - matches) / 2
        for (match in 0 until matches) {
            slots[round][slotDifference + match] =
                PairingSlot(PairingSlot.SlotType.ENCOUNTER, getPairingInfo(tournament, round, match))
        }
    }

    private fun getPairingInfo(tournament: LobbyTournament, round: Int, match: Int): PairingInfo {
        val currentRound = tournament.currentRound()!!

        if (currentRound.count == round) {
            val encounter: TournamentEncounter = currentRound.findEncounter(match)!!
            val firstTeam: TournamentTeam = encounter.firstTeam
            val secondTeam: TournamentTeam = encounter.secondTeam
            val display = "§e" + getDisplay(firstTeam) + " §cVS §e" + getDisplay(secondTeam)
            val lore: MutableList<String> =
                Lists.newArrayListWithExpectedSize(1 + firstTeam.participants.size + secondTeam.participants.size)
            // TODO: Instead of new lines for everyone use a comma separated text, which will be splitted if it's too long
            lore.addAll(firstTeam.participants.stream().map { participant -> "§7- §e" + participant.name }
                .collect(Collectors.toList()))
            lore.add("§7- §cVS §7-")
            lore.addAll(secondTeam.participants.stream().map { participant -> "§7- §e" + participant.name }
                .collect(Collectors.toList()))
            return PairingInfo(round, display, lore.toTypedArray(), encounter)
        }
        return PairingInfo(round, "§e? §cVS §e?", arrayOfNulls(0))
    }

    private fun getDisplay(team: TournamentTeam): String {
        return if (team.isEmpty()) {
            team.id.toString()
        } else team.participants.iterator().next().name + "(" + team.id + ")"
    }

    init {
        firstRoundMatches = tournament.settings().matchCount(1)
        val rounds = ceil(ln(tournament.settings().teamsAmount.toDouble()) / ln(2.0)).toInt()

        slots = Array(rounds + 1) { arrayOfNulls(firstRoundMatches + 1) }
        for (round in 0 until rounds) {
            setSlots(round, tournament)
        }
    }
}
