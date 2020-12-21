package de.bytemc.tournaments.server

import de.bytemc.tournaments.server.listener.ServicesListener
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule

/**
 * @author Nico_ND1
 */
class TournamentsServerModule : ICloudModule {

    override fun onEnable() {
        CloudAPI.instance.getEventManager().registerListener(this, ServicesListener())
    }

    override fun onDisable() {
    }
}
