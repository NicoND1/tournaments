package de.bytemc.tournaments.server.protocol.team

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
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
        val tournament = ServerTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(BooleanResult(addToTournament(tournament)))
        }
        buffer.release() // TODO: Release everytime
        return success(BooleanResult.FALSE)
    }

    private fun addToTournament(tournament: ServerTournament): Boolean {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return addToTeam(tournament, team)
            }
        }

        buffer.release()
        return false
    }

    private fun addToTeam(tournament: ServerTournament, team: TournamentTeam): Boolean {
        val participant = readParticipant()
        if (team.participants.contains(participant)) {
            buffer.release()
            return false
        }

        if (team.participants.size == tournament.settings().teamsOption.playersPerTeam) {
            buffer.release()
            return false
        }

        if (tournament.findTeam(participant.uuid) != null) {
            buffer.release()
            return false
        }

        team.participantsLock.withLock { team.participants.add(participant) }
        tournament.sendUnitPacket(PacketOutAddTeamParticipant(tournament, team, participant)).await()
        return true
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
