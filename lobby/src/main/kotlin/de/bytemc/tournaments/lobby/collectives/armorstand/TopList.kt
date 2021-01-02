package de.bytemc.tournaments.lobby.collectives.armorstand

import de.bytemc.tournaments.lobby.collectives.ICollectives
import net.codergames.responsive.hologram.Hologram
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.EquipmentSlot

/**
 * @author Nico_ND1
 */
class TopList(collectives: ICollectives, section: ConfigurationSection) {

    private val holograms: ArrayList<Hologram> = ArrayList()

    init {
        for (key in section.getKeys(false)) {
            val subSection = section.getConfigurationSection(key)
            val topListArmorStand = TopListArmorStand(subSection)

            val hologram = TopListHologram(collectives.repository())
            hologram.spawn(topListArmorStand.location)
            hologram.armorStand.isVisible = true
            hologram.armorStand.setBasePlate(false)
            hologram.armorStand.setArms(true)

            EquipmentSlot.values().forEach { hologram.updateEquipment(it) }
            holograms.add(hologram)
            topListArmorStand.apply(hologram.armorStand)
        }
    }

    fun updateAll() {
        for (hologram in holograms) {
            hologram.run()
            hologram.updateEquipment(EquipmentSlot.HEAD)
        }
    }

}
