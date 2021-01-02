package de.bytemc.tournaments.game

/**
 * @author Nico_ND1
 */
abstract class TimeStep(val duration: Long) {

    abstract fun trigger()

}
