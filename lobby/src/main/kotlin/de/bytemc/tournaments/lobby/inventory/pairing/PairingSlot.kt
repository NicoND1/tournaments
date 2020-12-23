package de.bytemc.tournaments.lobby.inventory.pairing

/**
 * @author Nico_ND1
 */
class PairingSlot(val slotType: SlotType, val info: PairingInfo) {

    enum class SlotType {
        ENCOUNTER, FINAL
    }
}
