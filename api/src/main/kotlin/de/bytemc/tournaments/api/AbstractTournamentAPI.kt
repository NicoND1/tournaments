package de.bytemc.tournaments.api

import java.util.*
import kotlin.collections.ArrayList

/**
 * @author Nico_ND1
 */
abstract class AbstractTournamentAPI<Tournament> : ITournamentAPI<Tournament> where Tournament : ITournament {

    private val tournaments: ArrayList<Tournament> = arrayListOf()

    override fun tournaments(): List<Tournament> {
        return ArrayList(tournaments)
    }

    override fun tournaments(state: TournamentState): List<Tournament> {
        return tournaments().filter { tournament -> tournament.state() == state }
    }

    override fun findTournament(id: UUID): Tournament? {
        return tournaments.first { tournament -> tournament.id() == id }
    }

    override fun findTournamentByCreator(creatorUUID: UUID): Tournament? {
        return tournaments.first { tournament -> tournament.creator().uuid == creatorUUID }
    }

}
