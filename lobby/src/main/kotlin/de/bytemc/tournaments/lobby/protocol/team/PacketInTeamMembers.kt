package de.bytemc.tournaments.lobby.protocol.team

import de.bytemc.tournaments.api.PlayerTexture
import de.bytemc.tournaments.api.TournamentParticipant
import de.bytemc.tournaments.api.readString
import de.bytemc.tournaments.api.readUUID
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInTeamMembers : BytePacket() {
    override suspend fun handle(connection: IConnection): ICommunicationPromise<Unit> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)
        if (tournament != null) {
            readTeams(tournament)
        }
        return unit()
    }

    private fun readTeams(tournament: LobbyTournament) {
        val teamSize = buffer.readInt()

        for (i in 0 until teamSize) {
            val teamID = buffer.readInt()
            val participantSize = buffer.readInt()
            val participants: ArrayList<TournamentParticipant> = ArrayList(participantSize)

            for (i1 in 0 until participantSize) {
                participants.add(readParticipant())
            }

            val team = tournament.findTeam(teamID)
            team?.participants?.clear()
            team?.participants?.addAll(participants)
        }

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
