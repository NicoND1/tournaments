package de.bytemc.tournaments.server.protocol.lobby

import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketInSendToTournamentLobby : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val tournamentID = readUUID()
        val participantID = readUUID()

        for (tournament in ServerTournamentAPI.instance.tournaments()) {
            if (tournament.id() == tournamentID) {
                handle(participantID, tournament)
            }
        }
        return unit()
    }

    private fun handle(participantID: UUID, tournament: ServerTournament) {
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(participantID) ?: return
        val service = tournament.getTournamentLobby() ?: return

        cloudPlayer.connect(service)
    }
}
