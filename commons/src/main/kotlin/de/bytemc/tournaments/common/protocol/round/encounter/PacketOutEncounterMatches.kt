package de.bytemc.tournaments.common.protocol.round.encounter

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutEncounterMatches(val tournament: ITournament) : BytePacket() {

    init {
        writeUUID(tournament.id())
        val encounters = tournament.currentRound()!!.encounters
        buffer.writeInt(encounters.size)
        for (encounter in encounters) {
            buffer.writeInt(encounter.id)
            buffer.writeBoolean(encounter.serviceID != null)
            if (encounter.serviceID != null) {
                writeUUID(encounter.serviceID!!)
            }
        }
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
