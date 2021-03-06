package de.bytemc.tournaments.api

import java.util.*

/**
 * @author Nico_ND1
 */
interface ITournament {

    fun id(): UUID

    fun creator(): TournamentCreator

    fun settings(): TournamentSettings

    fun teams(): List<TournamentTeam>

    fun state(): TournamentState

    fun currentRound(): TournamentRound?

    fun findTeam(id: Int): TournamentTeam?

    fun findTeam(participantID: UUID): TournamentTeam?

    fun isFull(): Boolean

}
