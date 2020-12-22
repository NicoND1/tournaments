package de.bytemc.tournaments.server.protocol

import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.readUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInDeleteTournament : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val tournamentID = readUUID()
        val tournament = ServerTournamentAPI.instance.findTournament(tournamentID)
            ?: return failure(NullPointerException("Couldn't find tournament $tournamentID"))

        ServerTournamentAPI.instance.deleteTournament(tournament)
        return unit()
    }
}
