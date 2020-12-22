package de.bytemc.tournaments.api

import java.util.*

/**
 * @author Nico_ND1
 */
interface ITournamentAPI<Tournament> where Tournament : ITournament {

    fun tournaments(): List<Tournament>

    fun tournaments(state: TournamentState): List<Tournament>

    fun findTournament(id: UUID): Tournament?

    fun findTournamentByCreator(creatorUUID: UUID): Tournament?

    fun allGames(): List<TournamentGame>

}
