package de.bytemc.tournaments.lobby.collectives

import de.bytemc.core.playerutils.scoreboard.ByteScoreboardBuilder
import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

/**
 * @author Nico_ND1
 */
class CollectivesScoreboard(private val collectivesPlayer: CollectivesPlayer, collectives: ICollectives) {

    private val player = collectivesPlayer.player
    private val scoreboard = ByteScoreboardBuilder(player)
    var joined = false

    init {
        update(collectives)
    }

    fun update(collectives: ICollectives) {
        collectivesPlayer.bytePlayer.initialScoreboard()
        scoreboard.setLine(10, "§r", "§r")
        setCurrentRound()
        scoreboard.setLine(7, "§r§r", "§r")
        scoreboard.setLine(6, "§7Vorherige ", "§7Gegner:")
        writeTeamNames(5, previousTeams())
        if (joined) setTablist(collectives)
    }

    fun join(collectives: ICollectives) {
        joined = true

        scoreboard.setBoard("${collectivesPlayer.color()}Turniere")
        setTablist(collectives)
    }

    private fun previousTeams(): List<TournamentTeam> {
        val teams = ArrayList<TournamentTeam>()
        if (collectivesPlayer.tournament.currentRound() == null) {
            return teams
        }
        previousTeams(collectivesPlayer.tournament.currentRound()!!, teams)
        return teams
    }

    private fun previousTeams(round: TournamentRound, list: ArrayList<TournamentTeam>) {
        val team = collectivesPlayer.getTeam()
        for (encounter in round.encounters) {
            if (encounter.firstTeam == team) {
                list.add(encounter.secondTeam)
            } else if (encounter.secondTeam == team) {
                list.add(encounter.firstTeam)
            }
        }

        if (round.parentRound != null) {
            previousTeams(round.parentRound!!, list)
        }
    }

    private fun setCurrentRound() {
        val tournament = collectivesPlayer.tournament
        val roundCount = tournament.currentRound()?.count ?: 0

        scoreboard.setLine(9, "§7Aktuelle ", "§7Runde:")
        scoreboard.setLine(8, "§e$roundCount", "§r")
    }

    private fun writeTeamNames(startIndex: Int, teams: List<TournamentTeam>) {
        if (teams.isEmpty()) {
            scoreboard.setLine(startIndex, "§cNicht ", "§cvorhanden")
            scoreboard.setLine(startIndex - 1, "§r§r§r", "§r")
            return
        }

        val tournament = collectivesPlayer.tournament
        val isMultiTeam = tournament.settings().teamsOption.playersPerTeam > 1
        var index = startIndex
        for (team in teams) {
            if (isMultiTeam) {
                scoreboard.setLine(index--, "#${team.id}", "§r")
            } else {
                val name = if (team.participants.isEmpty()) "-" else team.participants[0].name
                scoreboard.setLine(index, name, "§r")
            }
        }
    }

    private fun setTablist(collectives: ICollectives) {
        val scoreboard = player.scoreboard
        val tournament = collectivesPlayer.tournament
        val ownTeam = collectivesPlayer.getTeam()

        for (team in tournament.teams()) {
            val sameTeam = ownTeam == team
            val color = if (sameTeam) "§a" else "§e"
            val order = if (sameTeam) "000" else String.format("%04d", team.id)
            val deadColor = if (sameTeam) "§a" else "§7"
            val deadOrder = "${order}dead"

            val scoreboardTeam = getOrCreateTeam(scoreboard, order)
            scoreboardTeam.prefix = "#${team.id} $color"

            val deadScoreboardTeam = getOrCreateTeam(scoreboard, deadOrder)
            deadScoreboardTeam.prefix = "#${team.id} $deadColor"

            for (participant in team.participants) {
                val player = collectives.repository().findPlayer(participant.uuid)
                if (player != null && player.isPlaying) {
                    scoreboardTeam.addEntry(participant.name)
                } else {
                    scoreboardTeam.removeEntry(participant.name)
                    deadScoreboardTeam.addEntry(participant.name)
                }
            }
        }
    }

    private fun getOrCreateTeam(scoreboard: Scoreboard, name: String): Team {
        return scoreboard.getTeam(name) ?: scoreboard.registerNewTeam(name)
    }

}
