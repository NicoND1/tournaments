package de.bytemc.tournaments.server.command.suggestion

import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.api.command.ICommandSender
import eu.thesimplecloud.launcher.console.command.provider.ICommandSuggestionProvider

/**
 * @author Nico_ND1
 */
class GameSuggestionProvider : ICommandSuggestionProvider {
    override fun getSuggestions(sender: ICommandSender, fullCommand: String, lastArgument: String): List<String> {
        return ServerTournamentAPI.instance.allGames().map { it.name }
    }
}
