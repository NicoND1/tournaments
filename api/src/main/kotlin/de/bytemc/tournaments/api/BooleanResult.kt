package de.bytemc.tournaments.api

/**
 * @author Nico_ND1
 */
data class BooleanResult constructor(val result: Boolean) {
    companion object {
        val TRUE = BooleanResult(true)
        val FALSE = BooleanResult(false)

        fun from(boolean: Boolean): BooleanResult {
            return if (boolean) TRUE else FALSE
        }
    }
}
