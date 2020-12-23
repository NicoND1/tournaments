package de.bytemc.tournaments.server.round

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament
import eu.thesimplecloud.launcher.startup.Launcher

/**
 * @author Nico_ND1
 */
class RoundPreparer(private val tournament: ServerTournament, private val count: Int) {

    fun prepareRound() = TournamentRound(count, calculateEncounters())

    private fun calculateEncounters(): Array<TournamentEncounter> {
        val matchCount = tournament.settings().matchCount(count)
        val teams = getTeams()
        shuffleTeams(teams)

        val encounters: Array<TournamentEncounter?> = arrayOfNulls(teams.size / 2)
        var indexCount = 0
        for ((idCount, i) in (0..teams.size step 2).withIndex()) {
            if (i == teams.size) continue
            Launcher.Companion.instance.logger.info("teamsSize=${teams.size} i=$i idCount=$idCount indexCount=$indexCount encounters=${encounters.size} matchCount=$matchCount")
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

    private fun getTeams(): ArrayList<TournamentTeam> {
        return if (tournament.currentRound() == null) {
            ArrayList(tournament.teams())
        } else {
            val currentRound = tournament.currentRound()
            val teams: ArrayList<TournamentTeam> = ArrayList()
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
