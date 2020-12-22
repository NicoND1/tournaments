package de.bytemc.tournaments.common.protocol.round.encounter

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.api.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutWinEncounter(tournament: ITournament, encounter: TournamentEncounter, winnerTeam: TournamentTeam) :
    BytePacket() {

    init {
        writeUUID(tournament.id())
        buffer.writeInt(encounter.id)
        buffer.writeInt(winnerTeam.id)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        return unit()
    }
}
