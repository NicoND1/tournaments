package de.bytemc.tournaments.common.broadcast

import eu.thesimplecloud.api.player.ICloudPlayer

/**
 * @author Nico_ND1
 */
class AllBroadcastMessage(private val message: String) : BroadcastMessage {
    override fun message(player: ICloudPlayer): String {
        return message
    }
}
