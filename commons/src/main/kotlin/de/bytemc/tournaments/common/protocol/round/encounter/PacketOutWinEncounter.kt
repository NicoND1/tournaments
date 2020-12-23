package de.bytemc.tournaments.common.protocol.round.encounter

import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.api.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketOutWinEncounter(tournamentID: UUID, encounterID: Int, winnerTeam: TournamentTeam) :
    BytePacket() {

    init {
        writeUUID(tournamentID)
        buffer.writeInt(encounterID)
        buffer.writeInt(winnerTeam.id)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }
}
