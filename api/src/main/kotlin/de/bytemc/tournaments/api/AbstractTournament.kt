package de.bytemc.tournaments.api

import java.util.*

/**
 * @author Nico_ND1
 */
abstract class AbstractTournament(
    private val id: UUID,
    private val creator: TournamentCreator,
    private val settings: TournamentSettings,
    private val teams: List<TournamentTeam>,
) : ITournament {

    var currentRound: TournamentRound? = null
    var currentState: TournamentState = TournamentState.COLLECTING

    override fun id() = id
    override fun creator() = creator
    override fun settings() = settings
    override fun teams() = teams
    override fun state() = currentState

    override fun currentRound() = currentRound

    override fun findTeam(id: Int) = teams.firstOrNull { it.id == id }

}
