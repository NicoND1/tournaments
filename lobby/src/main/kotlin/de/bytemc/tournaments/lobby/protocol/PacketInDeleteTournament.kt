package de.bytemc.tournaments.lobby.protocol

import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInDeleteTournament : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val tournamentID = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(tournamentID)
            ?: return failure(NullPointerException("Couldn't find tournament $tournamentID"))

        LobbyTournamentAPI.instance.deleteTournament(tournament)
        return unit()
    }
}
