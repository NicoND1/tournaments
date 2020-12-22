package de.bytemc.tournaments.lobby

import de.bytemc.core.ByteAPI
import de.bytemc.core.playerutils.BytePlayer
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Nico_ND1
 */
fun Player.primaryColor(): String {
    return findBytePlayer(uniqueId)?.firstColor ?: "§6"
}

fun Player.secondaryColor(): String {
    return findBytePlayer(uniqueId)?.secondColor ?: "§6"
}

fun Player.format(argument: String): String {
    return "§8» ${secondaryColor()}$argument"
}

private fun findBytePlayer(uuid: UUID): BytePlayer? {
    return ByteAPI.getInstance().bytePlayerManager.players[uuid]
}
