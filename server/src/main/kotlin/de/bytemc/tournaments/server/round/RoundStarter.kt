package de.bytemc.tournaments.server.round

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentMap
import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.broadcast
import de.bytemc.tournaments.server.broadcast.BroadcastMessage
import de.bytemc.tournaments.server.broadcast.secondaryColor
import de.bytemc.tournaments.server.round.promises.ServiceCreatePromiseListener
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.servicegroup.ICloudServiceGroup
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom

/**
 * @author Nico_ND1
 */
class RoundStarter(val tournament: ServerTournament, val round: TournamentRound) {

    companion object {
        val EXECUTOR: ExecutorService = Executors.newSingleThreadExecutor()
    }

    fun start() {
        val serviceName = tournament.settings().teamsOption.serviceGroupName
        val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName(serviceName)
            ?: throw NullPointerException("Couldn't find service group for $serviceName")

        EXECUTOR.submit { startSync(serviceGroup) }
    }

    private fun startSync(serviceGroup: ICloudServiceGroup) {
        val configuration = serviceGroup.createStartConfiguration()
        for (encounter in round.encounters) {
            if (encounter.firstTeam.isEmpty()) {
                handleEmptyEncounter(encounter, encounter.firstTeam)
            } else if (encounter.secondTeam.isEmpty()) {
                handleEmptyEncounter(encounter, encounter.secondTeam)
            } else {
                val promise = configuration.startService()
                promise.addCompleteListener(ServiceCreatePromiseListener(tournament, encounter, pollMap()))
            }
        }
    }

    private fun pollMap(): TournamentMap {
        val random = ThreadLocalRandom.current()
        val maps = tournament.settings().maps

        return maps[random.nextInt(maps.size)]
    }

    private fun handleEmptyEncounter(encounter: TournamentEncounter, emptyTeam: TournamentTeam) {
        val otherTeam = encounter.otherTeam(emptyTeam)

        otherTeam.broadcast(object : BroadcastMessage {
            override fun message(player: ICloudPlayer): String {
                return "§aIhr habt diese ${player.secondaryColor()}Turnier Runde §agewonnen, weil das andere Team nicht spielen kann."
            }
        })
    }

}
