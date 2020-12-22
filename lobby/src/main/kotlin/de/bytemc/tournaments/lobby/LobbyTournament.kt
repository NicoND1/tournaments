package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.api.*
import java.util.*

/**
 * @author Nico_ND1
 */
class LobbyTournament(
    id: UUID,
    creator: TournamentCreator,
    settings: TournamentSettings,
    teams: List<TournamentTeam>,
) : AbstractTournament(id, creator, settings, teams) {

    override fun setState(newState: TournamentState): Boolean {
        TODO("Not yet implemented")
    }

}
