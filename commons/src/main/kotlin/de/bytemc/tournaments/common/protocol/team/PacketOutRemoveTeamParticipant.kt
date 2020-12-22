package de.bytemc.tournaments.common.protocol.team

import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.api.writeUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketOutRemoveTeamParticipant(
    tournament: ITournament,
    team: TournamentTeam,
    participant: TournamentParticipant,
) : BytePacket() {

    init {
        writeUUID(tournament.id())
        buffer.writeInt(team.id)
        writeUUID(participant.uuid)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        return unit()
    }

}
