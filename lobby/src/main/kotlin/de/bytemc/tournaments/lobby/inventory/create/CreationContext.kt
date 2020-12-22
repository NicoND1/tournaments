package de.bytemc.tournaments.lobby.inventory.create

import de.bytemc.tournaments.api.TournamentGame
import de.bytemc.tournaments.api.TournamentMap
import de.bytemc.tournaments.api.TournamentSettings
import de.bytemc.tournaments.api.TournamentTeamsOption

/**
 * @author Nico_ND1
 */
class CreationContext private constructor(
    val game: TournamentGame,
    val maps: List<TournamentMap>,
    val teamsOption: TournamentTeamsOption,
    val teamsAmount: Int,
) {

    data class Builder(
        var game: TournamentGame? = null,
        var maps: ArrayList<TournamentMap> = arrayListOf(),
        var teamsOption: TournamentTeamsOption? = null,
        var teamsAmount: Int? = null,
    ) {
        fun game(game: TournamentGame) = apply { this.game = game }
        fun teamsOption(teamsOption: TournamentTeamsOption) = apply { this.teamsOption = teamsOption }
        fun teamsAmount(teamsAmount: Int) = apply { this.teamsAmount = teamsAmount }

        fun addMap(map: TournamentMap) = apply { maps.add(map) }
        fun removeMap(map: TournamentMap) = apply { maps.remove(map) }

        fun build() = TournamentSettings(game!!, maps, teamsOption!!, teamsAmount!!)
    }

}
