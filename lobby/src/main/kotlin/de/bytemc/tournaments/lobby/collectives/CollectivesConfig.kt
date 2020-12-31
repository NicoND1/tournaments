package de.bytemc.tournaments.lobby.collectives

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection

/**
 * @author Nico_ND1
 */
class CollectivesConfig(section: ConfigurationSection) {

    val spawnLocation: Location

    init {
        val world = Bukkit.getWorld(section.getString("world"))
        val x = section.getDouble("x")
        val y = section.getDouble("y")
        val z = section.getDouble("z")
        val yaw = section.getInt("yaw").toFloat()
        val pitch = section.getInt("pitch").toFloat()
        spawnLocation = Location(world, x, y, z, yaw, pitch)
    }

}
