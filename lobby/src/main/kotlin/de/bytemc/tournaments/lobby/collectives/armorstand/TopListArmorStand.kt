package de.bytemc.tournaments.lobby.collectives.armorstand

/*import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.ArmorStand
import org.bukkit.util.EulerAngle

/**
 * @author Nico_ND1
 */
class TopListArmorStand(private val section: ConfigurationSection) {

    val location = readLocation()
    val eulerAngles: Array<EulerAngle> = readEulerAngles()

    fun apply(armorStand: ArmorStand) {
        armorStand.teleport(location)
        for ((index, angle) in EulerAngleEnum.values().withIndex()) {
            angle.apply(armorStand, eulerAngles[index])
        }
    }

    private fun readLocation(): Location {
        val section = section.getConfigurationSection("location")
        val world = Bukkit.getWorld(section.getString("world"))
        return Location(world,
            section.getDouble("x"),
            section.getDouble("y"),
            section.getDouble("z"),
            section.getInt("yaw").toFloat(),
            section.getInt("pitch").toFloat()
        )
    }

    private fun readEulerAngles(): Array<EulerAngle> {
        val section = section.getConfigurationSection("pose")
        val array = arrayOfNulls<EulerAngle>(EulerAngleEnum.values().size)
        for ((index, _) in section.withIndex()) {
            //array[index] = readEulerAngle(section[index])
            // TODO
        }

        return Array(array.size) { i -> array[i]!! }
    }

    private fun readEulerAngle(section: ConfigurationSection): EulerAngle {
        return EulerAngle(section.getDouble("x"), section.getDouble("y"), section.getDouble("z"))
    }

    enum class EulerAngleEnum(private val function: (ArmorStand, EulerAngle) -> Unit) {
        HEAD({ armorStand, eulerAngle -> armorStand.bodyPose = eulerAngle }),
        LEFT_ARM({ armorStand, eulerAngle -> armorStand.leftArmPose = eulerAngle }),
        RIGHT_ARM({ armorStand, eulerAngle -> armorStand.rightArmPose = eulerAngle }),
        BODY({ armorStand, eulerAngle -> armorStand.bodyPose = eulerAngle }),
        LEFT_LEG({ armorStand, eulerAngle -> armorStand.leftLegPose = eulerAngle }),
        RIGHT_LEG({ armorStand, eulerAngle -> armorStand.rightLegPose = eulerAngle });

        fun apply(armorStand: ArmorStand, eulerAngle: EulerAngle) = function.invoke(armorStand, eulerAngle)

    }
}
*/
