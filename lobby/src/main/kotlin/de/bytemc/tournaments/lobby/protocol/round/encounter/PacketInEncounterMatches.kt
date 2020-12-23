package de.bytemc.tournaments.lobby.protocol.round.encounter

import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketInEncounterMatches : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)
        if (tournament == null) {
            buffer.release()
            return unit()
        }

        val size = buffer.readInt()
        for (i in 0 until size) {
            val encounterID = buffer.readInt()
            val hasServiceID = buffer.readBoolean()

            if (hasServiceID) {
                updateServiceID(tournament, encounterID, readUUID())
            }
        }
        return unit()
    }

    private fun updateServiceID(tournament: LobbyTournament, encounterID: Int, serviceID: UUID) {
        tournament.currentRound()!!.findEncounter(encounterID).let {
            it?.serviceID = serviceID
        }
    }

}
