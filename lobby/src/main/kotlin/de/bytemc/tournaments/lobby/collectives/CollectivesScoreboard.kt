package de.bytemc.tournaments.lobby.collectives

import de.bytemc.core.playerutils.scoreboard.ByteScoreboardBuilder
import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.collectives.player.CollectivesPlayer
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import org.bukkit.util.ChatPaginator
import java.util.stream.Collectors

/**
 * @author Nico_ND1
 */
class CollectivesScoreboard(private val collectivesPlayer: CollectivesPlayer) {

    companion object {
        private val colors = arrayOf("b", "d", "e", "f", "c")
    }

    private val player = collectivesPlayer.player
    private val scoreboard = ByteScoreboardBuilder(player)

    init {
        update()
    }

    fun update() {
        collectivesPlayer.bytePlayer.initialScoreboard()
        scoreboard.setLine(10, "§r", "§r")
        setCurrentRound()
        scoreboard.setLine(7, "§r§r", "§r")
        scoreboard.setLine(6, "§7Vorherige ", "§7Gegner:")
        writeTeamNames(5, previousTeams())
        scoreboard.setBoard("${collectivesPlayer.color()}Turniere")

        setTablist()
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

        var index = startIndex
        val allNames = teams.flatMapIndexed { i: Int, team: TournamentTeam ->
            team.participants.map { "${colors[i]}${it.name}" }
        }.stream().collect(Collectors.joining(", "))

        val paginated = ChatPaginator.wordWrap(allNames, 28)
        for (i in paginated.indices) {
            val page = paginated[i]
            val prefix = if (page.length > 16) page.substring(0, 15) else page
            val suffix = if (page.length > 16) page.substring(15) else "§r"

            scoreboard.setLine(index--, prefix, suffix)
        }
        scoreboard.setLine(index, "§r§r§r", "§r")
    }

    private fun setTablist() {
        val scoreboard = player.scoreboard
        val tournament = collectivesPlayer.tournament
        val ownTeam = collectivesPlayer.getTeam()

        for (team in tournament.teams()) {
            val sameTeam = ownTeam == team
            val color = if (sameTeam) "§a" else "§e"
            val order = if (sameTeam) "000" else String.format("%04d", team.id)

            val scoreboardTeam = getOrCreateTeam(scoreboard, order)
            scoreboardTeam.prefix = "#${team.id} $color"

            for (participant in team.participants) {
                scoreboardTeam.addEntry(participant.name)
            }
        }
    }

    private fun getOrCreateTeam(scoreboard: Scoreboard, name: String): Team {
        return scoreboard.getTeam(name) ?: scoreboard.registerNewTeam(name)
    }

}
