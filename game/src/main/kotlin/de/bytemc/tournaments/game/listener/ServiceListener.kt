package de.bytemc.tournaments.game.listener

import de.bytemc.tournaments.game.TournamentGamePlugin
import eu.thesimplecloud.api.event.service.CloudServiceInvisibleEvent
import eu.thesimplecloud.api.eventapi.CloudEventHandler
import eu.thesimplecloud.api.eventapi.IListener
import eu.thesimplecloud.plugin.startup.CloudPlugin

/**
 * @author Nico_ND1
 */
class ServiceListener(private val plugin: TournamentGamePlugin) : IListener {

    @CloudEventHandler
    fun handle(event: CloudServiceInvisibleEvent) {
        val service = event.cloudService
        if (service.getUniqueId() == CloudPlugin.instance.thisService().getUniqueId()) {
            plugin.notifyIngame()
        }
    }

}
