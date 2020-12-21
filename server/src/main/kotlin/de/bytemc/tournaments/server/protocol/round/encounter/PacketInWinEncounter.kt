package de.bytemc.tournaments.server.protocol.round.encounter

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
import de.bytemc.tournaments.server.readUUID
import de.bytemc.tournaments.server.sendUnitPacket
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
                encounter.winnerTeam = encounter.firstTeam
                notifyWin(tournament, encounter, encounter.firstTeam)
                success(true)
            }
            encounter.secondTeam.id == teamID -> {
                encounter.winnerTeam = encounter.secondTeam
                notifyWin(tournament, encounter, encounter.secondTeam)
                success(true)
            }
            else -> {
                failure(NullPointerException("Couldn't find winner team for $tournament $encounter $teamID"))
            }
        }
    }

    private fun notifyWin(tournament: ServerTournament, encounter: TournamentEncounter, winnerTeam: TournamentTeam) {
        val packet = PacketOutWinEncounter(tournament, encounter, winnerTeam)
        tournament.sendUnitPacket(packet)
    }
}
