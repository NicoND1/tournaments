package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.TournamentState
import java.util.concurrent.TimeUnit

/**
 * @author Nico_ND1
 */
class TournamentDeletionRunnable : Runnable {

    private val timeout = TimeUnit.MINUTES.toMillis(1)

    override fun run() {
        for (tournament in ServerTournamentAPI.instance.tournaments(TournamentState.FINISHED)) {
            if (tournament.endTime != 0L && tournament.endTime < System.currentTimeMillis() + timeout) {
                ServerTournamentAPI.instance.deleteTournament(tournament)
            }
        }
    }
}
