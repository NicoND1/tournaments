package de.bytemc.tournaments.server.protocol.round.encounter

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.encounter.setWinnerTeam
import de.bytemc.tournaments.server.readUUID
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * @author Nico_ND1
 */
class PacketInWinEncounter : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val id = readUUID()
        val tournament = ServerTournamentAPI.instance.findTournament(id)

        return if (tournament == null) {
            failure(NullPointerException("Couldn't find tournament for $id"))
        } else {
            findEncounter(tournament)
        }
    }

    private fun findEncounter(tournament: ServerTournament): ICommunicationPromise<Boolean> {
        val encounterID = buffer.readInt()
        val round = tournament.currentRound()

        if (round != null) {
            for (encounter in round.encounters) {
                if (encounter.id == encounterID) {
                    return findTeam(tournament, encounter)
                }
            }
        }

        return success(false)
    }

    private fun findTeam(tournament: ServerTournament, encounter: TournamentEncounter): ICommunicationPromise<Boolean> {
        val teamID = buffer.readInt()

        return when {
            encounter.firstTeam.id == teamID -> {
                encounter.setWinnerTeam(tournament, encounter.firstTeam)
                success(true)
            }
            encounter.secondTeam.id == teamID -> {
                encounter.setWinnerTeam(tournament, encounter.secondTeam)
                success(true)
            }
            else -> {
                failure(NullPointerException("Couldn't find winner team for $tournament $encounter $teamID"))
            }
        }
    }
}
