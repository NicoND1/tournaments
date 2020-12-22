package de.bytemc.tournaments.server.round.promises

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.handleError
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.event.service.CloudServiceStartedEvent
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromiseListener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author Nico_ND1
 */
class ServiceStartPromiseListener(
    private val tournament: ServerTournament,
    private val encounter: TournamentEncounter,
) : ICommunicationPromiseListener<CloudServiceStartedEvent> {

    companion object {
        val SCHEDULED_EXECUTOR: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

    override fun operationComplete(future: ICommunicationPromise<CloudServiceStartedEvent>) {
        if (future.isSuccess) {
            val event = future.get()
            val service = event.cloudService

            SCHEDULED_EXECUTOR.schedule({
                sendPlayers(encounter.firstTeam, service)
                sendPlayers(encounter.secondTeam, service)
            }, 3, TimeUnit.SECONDS)
        } else {
            future.throwFailure()
            encounter.handleError(tournament)
        }
    }

    private fun sendPlayers(team: TournamentTeam, service: ICloudService) {
        for (participant in team.participants) {
            val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(participant.uuid)
                ?: continue

            cloudPlayer.connect(service)
        }
    }
}
