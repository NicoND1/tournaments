package de.bytemc.tournaments.api

import java.util.*
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

/**
 * @author Nico_ND1
 */
data class TournamentCreator(val uuid: UUID, val name: String)

data class TournamentTeam(val id: Int, val participants: List<TournamentParticipant>) {
    fun isEmpty() = participants.isEmpty()
}

data class TournamentParticipant(val uuid: UUID, val name: String)

data class TournamentSettings(
    val game: TournamentGame,
    val maps: List<TournamentMap>,
    val teamsOption: TournamentTeamsOption,
    val teamsAmount: Int
) {
    fun maxRounds(): Int {
        return ceil(log10(teamsAmount.toDouble()) / log10(2.toDouble())).toInt()
    }
}

data class TournamentGame(
    val name: String,
    val color: String,
    val prettyName: String,
    val teamsOptions: List<TournamentTeamsOption>
)

data class TournamentTeamsOption(
    val playersPerTeam: Int,
    val mapPool: List<TournamentMap>,
    val serviceGroupName: String
)

data class TournamentMap(val name: String, val builders: Array<String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TournamentMap
        if (name != other.name) return false
        return true
    }

    override fun hashCode() = name.hashCode()
}

enum class TournamentState {
    COLLECTING,
    PLAYING,
    FINISHED
}

data class TournamentRound(val count: Int, val encounters: Array<TournamentEncounter>) {
    fun matchCount(settings: TournamentSettings): Int {
        return (settings.teamsAmount / 2.0.pow((count + 1).toDouble())).toInt()
    }

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
    val id: Int, val firstTeam: TournamentTeam, val secondTeam: TournamentTeam, var winnerTeam: TournamentTeam?
)
