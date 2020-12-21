package de.bytemc.tournaments.server.listener

import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.api.event.service.CloudServiceUnregisteredEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.api.screen.ICommandExecutable
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
    }

}
