package de.bytemc.tournaments.api

import java.util.*

/**
 * @author Nico_ND1
 */
data class TournamentMatchData(
    val tournamentID: UUID,
    val encounterID: Int,
    val firstTeam: TournamentTeam,
    val secondTeam: TournamentTeam,
    val map: TournamentMap,
) {
    constructor(tournament: ITournament, encounter: TournamentEncounter, map: TournamentMap) : this(tournament.id(),
        encounter.id,
        encounter.firstTeam,
        encounter.secondTeam,
        map)
}
