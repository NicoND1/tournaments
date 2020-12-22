package de.bytemc.tournaments.api

import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

/**
 * @author Nico_ND1
 */
data class TournamentCreator(val uuid: UUID, val name: String)

data class TournamentTeam(val id: Int, val participants: ArrayList<TournamentParticipant>) {
    val participantsLock = ReentrantLock()

    fun isEmpty() = participants.isEmpty()

    fun name(): String {
        return participants.stream().map { participant -> participant.name }.collect(Collectors.joining(", "))
    }
}

data class TournamentParticipant(val uuid: UUID, val name: String)

data class TournamentSettings(
    val game: TournamentGame,
    val maps: List<TournamentMap>,
    val teamsOption: TournamentTeamsOption,
    val teamsAmount: Int,
) {
    fun maxRounds(): Int {
        return ceil(log10(teamsAmount.toDouble()) / log10(2.toDouble())).toInt()
    }

    fun matchCount(roundCount: Int): Int {
        return (teamsAmount / 2.0.pow((roundCount).toDouble())).toInt()
    }
}

data class TournamentGame(
    val name: String,
    val color: String,
    val prettyName: String,
    val teamsOptions: ArrayList<TournamentTeamsOption>,
)

data class TournamentTeamsOption(
    val playersPerTeam: Int,
    val mapPool: ArrayList<TournamentMap>,
    val serviceGroupName: String,
)

data class TournamentMap(val name: String)

enum class TournamentState {
    COLLECTING,
    PLAYING,
    FINISHED
}

data class TournamentRound(val count: Int, val encounters: Array<TournamentEncounter>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TournamentRound
        if (count != other.count) return false
        return true
    }

    override fun hashCode() = count
}

data class TournamentEncounter(
    val id: Int, val firstTeam: TournamentTeam, val secondTeam: TournamentTeam, var winnerTeam: TournamentTeam? = null,
) {
    fun otherTeam(team: TournamentTeam): TournamentTeam {
        return if (team.id == firstTeam.id) {
            firstTeam
        } else {
            secondTeam
        }
    }
}
