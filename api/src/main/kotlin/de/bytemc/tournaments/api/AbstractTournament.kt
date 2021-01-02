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

    override fun findTeam(participantID: UUID): TournamentTeam? {
        return teams.firstOrNull { it.participants.any { par -> par.uuid == participantID } }
    }

    override fun isFull(): Boolean {
        return teams().all { team -> team.participants.size == settings().teamsOption.playersPerTeam }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AbstractTournament
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "AbstractTournament(id=$id, creator=$creator, settings=$settings, teams=$teams, currentRound=$currentRound, currentState=$currentState)"
    }


}
