package de.bytemc.tournaments.game

import org.bukkit.Bukkit

/**
 * @author Nico_ND1
 */
class NotificatorTimeStep(duration: Long, private val minutesLeft: Int) : TimeStep(duration) {
    override fun trigger() {
        val number = if (minutesLeft == 1) "" else "n"
        Bukkit.broadcastMessage("Damit das Turnier weiter gehen kann, habt ihr Gegner noch $minutesLeft Minute$number, bis diese Runde gestartet sein muss. Sonst gewinnt das Team, welches Online ist.")
    }
}
