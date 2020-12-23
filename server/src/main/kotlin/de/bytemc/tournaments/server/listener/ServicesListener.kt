package de.bytemc.tournaments.server.listener

import de.bytemc.tournaments.api.TournamentMatchData
import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.setWinnerTeam
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.api.screen.ICommandExecutable
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.base.manager.startup.Manager
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient

/**
 * @author Nico_ND1
 */
class ServicesListener : IListener {

    private val clientManager: IClientManager<ICommandExecutable> =
        Manager.instance.communicationServer.getClientManager()

    @CloudEventHandler
    fun onServiceRemove(event: CloudServiceUnregisteredEvent) {
        val client: IConnectedClient<ICommandExecutable>? = clientManager.getClientByClientValue(event.cloudService)
        if (client != null) {
            ServerTournamentAPI.instance.stopListening(client)
        }

        if (event.cloudService.hasProperty("tournamentMatch")) {
            checkTournamentService(event.cloudService)
        }
    }

    private fun checkTournamentService(service: ICloudService) {
        val property: IProperty<TournamentMatchData> = service.getProperty("tournamentMatch") ?: return
        val matchData = property.getValue()
        val tournament = ServerTournamentAPI.instance.findTournament(matchData.tournamentID) ?: return
        val round = tournament.currentRound() ?: return
        val encounter = round.findEncounter(matchData.encounterID) ?: return
        if (encounter.winnerTeam != null) return

        val winnerTeam = if (encounter.firstTeam.isEmpty()) encounter.secondTeam else encounter.firstTeam
        encounter.setWinnerTeam(tournament, winnerTeam)
    }

}
