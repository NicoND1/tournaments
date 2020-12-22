package de.bytemc.tournaments.lobby.command

import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.inventory.MainInventory
import de.bytemc.tournaments.lobby.inventory.ManageInventory
import de.bytemc.tournaments.lobby.inventory.OverviewInventory
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author Nico_ND1
 */
class TournamentCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        val hasPermission = sender.hasPermission("tournament.create")

        if (hasPermission && args.size == 1 && args[0] == "manage") {
            val tournament = LobbyTournamentAPI.instance.findTournamentByCreator(sender.uniqueId)

            if (tournament != null) {
                ManageInventory(sender, tournament).open(sender)
                return true
            }
        }

        if (hasPermission) {
            MainInventory(sender).open(sender)
        } else {
            OverviewInventory(sender).open(sender)
        }
        return true
    }
}
