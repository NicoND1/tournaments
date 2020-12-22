package de.bytemc.tournaments.server.round

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament

/**
 * @author Nico_ND1
 */
class RoundPreparer(private val tournament: ServerTournament, private val count: Int) {

    fun prepareRound(): TournamentRound = TournamentRound(count, calculateEncounters())

    private fun calculateEncounters(): Array<TournamentEncounter> {
        val matchCount = tournament.settings().matchCount(count)
        val teams = getTeams(matchCount / 2)
        shuffleTeams(teams)

        val encounters: Array<TournamentEncounter?> = arrayOfNulls(teams.size)
        var indexCount = 0
        for ((idCount, i) in (0..teams.size step 2).withIndex()) {
            val firstEncounter = TournamentEncounter(idCount, teams[i], teams[i + 1])

            encounters[indexCount++] = firstEncounter
        }

        return encounters.map { it!! }.toTypedArray()
    }

    private fun shuffleTeams(teams: ArrayList<TournamentTeam>) {
        val removedTeams = teams.filter { tournamentTeam -> tournamentTeam.participants.isEmpty() }
        teams.removeAll(removedTeams)
        teams.shuffle()
        teams.addAll(removedTeams)
    }

    private fun getTeams(expectedTeamAmount: Int): ArrayList<TournamentTeam> {
        return if (tournament.currentRound() == null) {
            ArrayList(tournament.teams())
        } else {
            val currentRound = tournament.currentRound()
            val teams: ArrayList<TournamentTeam> = ArrayList(expectedTeamAmount)
            for (encounter in currentRound!!.encounters) {
                if (encounter.winnerTeam == null) {
                    teams.add(encounter.firstTeam)
                    // TODO: Debug that we used a random team, because somehow no winner was present
                } else {
                    teams.add(encounter.winnerTeam!!)
                }
            }

            teams
        }
    }

}
