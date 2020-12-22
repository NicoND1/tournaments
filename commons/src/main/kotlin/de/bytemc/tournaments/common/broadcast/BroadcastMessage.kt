package de.bytemc.tournaments.common.broadcast

import eu.thesimplecloud.api.player.ICloudPlayer

/**
 * @author Nico_ND1
 */
interface BroadcastMessage {
    fun message(player: ICloudPlayer): String

    fun canReceive(player: ICloudPlayer): Boolean = true
}
