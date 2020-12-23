package de.bytemc.tournaments.lobby.protocol.team

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class PacketInAddTeamParticipant : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<BooleanResult> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(addToTournament(tournament))
        }
        return success(BooleanResult.FALSE)
    }

    private fun addToTournament(tournament: LobbyTournament): BooleanResult {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return addToTeam(team)
            }
        }

        return BooleanResult.FALSE
    }

    private fun addToTeam(team: TournamentTeam): BooleanResult {
        val participant = readParticipant()
        if (team.participants.contains(participant)) {
            return BooleanResult.FALSE
        }

        team.participantsLock.withLock { team.participants.add(participant) }
        // TODO: Update inventories
        return BooleanResult.TRUE
    }

    private fun readParticipant(): TournamentParticipant {
        val uuid = readUUID()
        val name = readString()

        if (buffer.readBoolean()) {
            val texture = PlayerTexture(readString(), readString())
            return TournamentParticipant(uuid, name, texture)
        }
        return TournamentParticipant(uuid, name)
    }

}
