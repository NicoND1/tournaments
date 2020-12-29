package de.bytemc.tournaments.lobby.command

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.inventory.MainInventory
import de.bytemc.tournaments.lobby.inventory.ManageInventory
import de.bytemc.tournaments.lobby.inventory.OverviewInventory
import eu.thesimplecloud.api.CloudAPI
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Nico_ND1
 */
class TournamentCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false

        if (args.size == 1 && args[0] == "rejoin") {
            rejoin(sender)
            return true
        }

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

    private fun rejoin(player: Player) {
        var team: TournamentTeam? = null
        var tournament: ITournament? = null
        for (to in LobbyTournamentAPI.instance.tournaments(TournamentState.PLAYING)) {
            for (te in to.teams()) {
                if (te.participants.any { par -> par.uuid == player.uniqueId }) {
                    tournament = to
                    team = te
                }
            }
        }

        if (team == null || tournament?.currentRound() == null) return
        val round = tournament.currentRound()

        for (encounter in round!!.encounters) {
            if (encounter.firstTeam.id == team.id || encounter.secondTeam.id == team.id) {
                if (encounter.serviceID != null) {
                    connect(encounter.serviceID!!, player)
                }
                return
            }
        }
    }

    private fun connect(serviceID: UUID, player: Player) {
        for (allCachedObject in CloudAPI.instance.getCloudServiceManager().getAllCachedObjects()) {
            if (allCachedObject.getUniqueId() == serviceID) {
                val cPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(player.uniqueId) ?: break
                CloudAPI.instance.getCloudPlayerManager().connectPlayer(cPlayer, allCachedObject)
                break
            }
        }
    }
}
