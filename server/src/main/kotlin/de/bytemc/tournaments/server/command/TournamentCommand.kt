package de.bytemc.tournaments.server.command

import de.bytemc.tournaments.api.TournamentCreator
import de.bytemc.tournaments.api.TournamentSettings
import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.command.suggestion.GameSuggestionProvider
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.launcher.console.command.CommandType
import eu.thesimplecloud.launcher.console.command.ICommandHandler
import eu.thesimplecloud.launcher.console.command.annotations.Command
import eu.thesimplecloud.launcher.console.command.annotations.CommandArgument
import eu.thesimplecloud.launcher.console.command.annotations.CommandSubPath
import eu.thesimplecloud.launcher.startup.Launcher

/**
 * @author Nico_ND1
 */
@Command(name = "tournamentdebug", commandType = CommandType.INGAME, permission = "", ["td"])
class TournamentCommand : ICommandHandler {

    @CommandSubPath(path = "create <game> <teamsize> <teamamount>")
    fun create(
        sender: ICloudPlayer,
        @CommandArgument(name = "game", suggestionProvider = GameSuggestionProvider::class) gameName: String,
        @CommandArgument(name = "teamsize") teamSize: Int,
        @CommandArgument(name = "teamamount") teamsAmount: Int,
    ) {
        if (teamsAmount % 2 != 0) {
            sender.sendMessage("Ich brauche eine gerade Team Anzahl")
            return
        }

        val game = ServerTournamentAPI.instance.allGames().firstOrNull { it.name == gameName }
        if (game == null) {
            sender.sendMessage("Das Spiel gibt es nicht")
            return
        }

        val teamsOption = game.teamsOptions.firstOrNull { it.playersPerTeam == teamSize }
        if (teamsOption == null) {
            sender.sendMessage("Diese Teamgröße finde ich nicht")
            return
        }

        val creator = TournamentCreator(sender.getUniqueId(), sender.getName())
        val settings = TournamentSettings(game, teamsOption.mapPool, teamsOption, teamsAmount)

        val tournament = ServerTournamentAPI.instance.createTournament(creator, settings)
        if (tournament == null) {
            sender.sendMessage("Konnte nicht erstellt werden")
        } else {
            sender.sendMessage("$tournament")
        }
    }

    @CommandSubPath(path = "debug")
    fun debug() {
        Launcher.instance.logger.info(ServerTournamentAPI.instance.toString())
    }

}
