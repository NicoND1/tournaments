package de.bytemc.tournaments.server.round.promises

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentMap
import de.bytemc.tournaments.api.TournamentMatchData
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.broadcast
import de.bytemc.tournaments.server.broadcast.BroadcastMessage
import de.bytemc.tournaments.server.broadcast.secondaryColor
import de.bytemc.tournaments.server.handleError
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromiseListener

/**
 * @author Nico_ND1
 */
class ServiceCreatePromiseListener(
    private val tournament: ServerTournament,
    private val encounter: TournamentEncounter,
    private val map: TournamentMap,
) : ICommunicationPromiseListener<ICloudService> {

    override fun operationComplete(future: ICommunicationPromise<ICloudService>) {
        if (future.isSuccess) {
            val service = future.get()
            val data = TournamentMatchData(tournament, encounter, map)

            service.setProperty("tournamentMatch", data)
            service.update()
            service.createStartedPromise().addCompleteListener(ServiceStartPromiseListener(tournament, encounter))

            encounter.broadcast(object : BroadcastMessage {
                override fun message(player: ICloudPlayer): String {
                    return "§aEuer Server für die ${player.secondaryColor()}Turnier Runde §astartet jetzt..."
                }
            })
        } else {
            future.throwFailure()
            encounter.handleError(tournament)
        }
    }
}
