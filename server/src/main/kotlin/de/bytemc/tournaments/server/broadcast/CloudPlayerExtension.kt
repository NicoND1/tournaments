package de.bytemc.tournaments.server.broadcast

import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.property.IProperty

/**
 * @author Nico_ND1
 */
fun ICloudPlayer.primaryColor(): String {
    val property: IProperty<String>? = getProperty("color")
    return property?.getValue() ?: "§6"
}

fun ICloudPlayer.secondaryColor(): String {
    val property: IProperty<String>? = getProperty("colorsub")
    return property?.getValue() ?: "§6"
}

fun ICloudPlayer.getPrefix(group: String): String {
    return "§8 » " + primaryColor() + "§l" + group + " §8┃ §7"
}
