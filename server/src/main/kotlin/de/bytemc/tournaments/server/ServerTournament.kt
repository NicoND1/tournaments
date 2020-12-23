package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.broadcast.AllBroadcastMessage
import de.bytemc.tournaments.common.broadcast.BroadcastMessage
import de.bytemc.tournaments.common.broadcast.secondaryColor
import de.bytemc.tournaments.common.protocol.round.PacketOutStartRound
import de.bytemc.tournaments.common.protocol.state.PacketOutSetState
import de.bytemc.tournaments.server.round.RoundPreparer
import de.bytemc.tournaments.server.round.RoundStarter
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class ServerTournament(
    id: UUID,
    creator: TournamentCreator,
    settings: TournamentSettings,
    teams: List<TournamentTeam>,
) : AbstractTournament(id, creator, settings, teams) {

    fun setState(newState: TournamentState): Boolean {
        if (currentState == newState) {
            return false
        }

        currentState = newState
        sendUnitPacket(PacketOutSetState(this, newState)).addCompleteListener {
            if (it.isSuccess) {
                handleStateChange(newState)
            } else {
                it.throwFailure()
            }
        }
        return true
    }

    fun delete() {
        if (state() == TournamentState.PLAYING) {
            val serviceGroup = CloudAPI.instance.getCloudServiceGroupManager()
                .getServiceGroupByName(settings().teamsOption.serviceGroupName) ?: return

            for (allService in serviceGroup.getAllServices()) {
                if (!allService.hasProperty("tournamentMatch")) continue

                val property = allService.getProperty<TournamentMatchData>("tournamentMatch") ?: continue
                val matchData = property.getValue()
                if (matchData.tournamentID == id()) {
                    allService.shutdown()
                    matchData.firstTeam.broadcast(AllBroadcastMessage("§cDas Turnier wurde gelöscht."))
                    matchData.secondTeam.broadcast(AllBroadcastMessage("§cDas Turnier wurde gelöscht."))
                }
            }
        }
    }

    private fun handleStateChange(state: TournamentState) {
        if (state == TournamentState.PLAYING) {
            startRound(1)
        }
    }

    private fun startRound(count: Int) {
        val round = RoundPreparer(this, count).prepareRound()
        currentRound = round

        sendUnitPacket(PacketOutStartRound(this, round)).addCompleteListener {
            val starter = RoundStarter(this, round)
            starter.start()
        }.addFailureListener {
            it.printStackTrace()

            broadcast(AllBroadcastMessage("Es trat ein Fehler beim Turnier auf, wodurch es automatisch beendet werden musste."))
            setState(TournamentState.FINISHED)
        }
    }

    fun testRoundOver() {
        val currentRound = currentRound() ?: return
        for (encounter in currentRound.encounters) {
            if (encounter.winnerTeam == null) {
                return
            }
        }

        val maxRounds = settings().maxRounds()
        if (maxRounds < currentRound.count + 1) {
            notifyWinner(currentRound)
        } else {
            broadcast(object : BroadcastMessage {
                override fun message(player: ICloudPlayer): String {
                    return "§aDie ${player.secondaryColor()}Turnier Runde §aist vorbei, die nächste beginnt in Kürze."
                }
            })

            startRound(currentRound.count + 1)
        }
    }

    private fun notifyWinner(currentRound: TournamentRound) {
        var winningTeam: TournamentTeam? = null
        if (currentRound.encounters.size == 1) {
            winningTeam = currentRound.encounters[0].winnerTeam
        }

        val winner = if (winningTeam == null) {
            "§cNiemand hat gewonnen."
        } else {
            "§aTeam ${winningTeam.name()} hat gewonnen"
        }

        broadcast(object : BroadcastMessage {
            override fun message(player: ICloudPlayer): String {
                return "§aDas ${player.secondaryColor()}Turnier §aist vorbei. $winner"
            }
        })
    }

    fun broadcast(message: BroadcastMessage) {
        teams().forEach { team -> team.broadcast(message) }
    }

    fun sendUnitPacket(packet: IPacket): ICommunicationPromise<Unit> {
        return ServerTournamentAPI.instance.sendUnitPacket(packet)
    }

    fun sendUnitPacket(packet: IPacket, connectionToIgnore: IConnection): ICommunicationPromise<Unit> {
        return ServerTournamentAPI.instance.sendUnitPacket(packet, connectionToIgnore)
    }

}
