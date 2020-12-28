package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.TournamentState

/**
 * @author Nico_ND1
 */
class TournamentDeletionRunnable : Runnable {
    override fun run() {
        for (tournament in ServerTournamentAPI.instance.tournaments(TournamentState.FINISHED)) {
            if (tournament.endTime != 0L && tournament.endTime < System.currentTimeMillis()) {
                ServerTournamentAPI.instance.deleteTournament(tournament)
            }
        }
    }
}
