package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.broadcast.AllBroadcastMessage
import de.bytemc.tournaments.common.broadcast.BroadcastMessage
import de.bytemc.tournaments.common.broadcast.secondaryColor
import de.bytemc.tournaments.common.protocol.round.PacketOutStartRound
import de.bytemc.tournaments.common.protocol.round.encounter.PacketOutEncounterMatches
import de.bytemc.tournaments.common.protocol.state.PacketOutSetState
import de.bytemc.tournaments.server.event.TournamentNextRoundEvent
import de.bytemc.tournaments.server.event.TournamentStateChangeEvent
import de.bytemc.tournaments.server.round.RoundPreparer
import de.bytemc.tournaments.server.round.RoundStarter
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.api.service.ICloudService
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * @author Nico_ND1
 */
class ServerTournament(
    id: UUID,
    creator: TournamentCreator,
    settings: TournamentSettings,
    teams: List<TournamentTeam>,
) : AbstractTournament(id, creator, settings, teams) {

    var endTime: Long = 0L
    var winningTeam: TournamentTeam? = null
    var tournamentLobby: String? = null

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
            CloudAPI.instance.getEventManager().call(TournamentStateChangeEvent(this))
            setTournamentLobby()
        } else if (state == TournamentState.FINISHED) {
            endTime = System.currentTimeMillis()
            CloudAPI.instance.getEventManager().call(TournamentStateChangeEvent(this))
        }
    }

    private fun setTournamentLobby() {
        val currentParticipantAmount = teams().sumBy { it.participants.size }
        val group = CloudAPI.instance.getCloudServiceGroupManager().getServiceGroupByName("Tournament") ?: return
        val services = group.getAllServices()

        if (services.isNotEmpty()) {
            val service = services.sortedByDescending {
                val service = it
                val usingTournaments = ServerTournamentAPI.instance.tournaments()
                    .filter { it.tournamentLobby == service.getName() }
                    .flatMap { it.teams() }
                    .sumBy { it.participants.size }

                if (service.getMaxPlayers() - usingTournaments > currentParticipantAmount) {
                    1
                } else {
                    -1
                }
            }.reversed().firstOrNull() ?: return

            tournamentLobby = service.getName()
        }
    }

    fun getTournamentLobby(): ICloudService? {
        if (tournamentLobby != null) {
            return CloudAPI.instance.getCloudServiceManager().getCloudServiceByName(tournamentLobby!!)
        }
        return null
    }

    fun moveToLobby(vararg teams: TournamentTeam) {
        val service = getTournamentLobby() ?: return

        for (team in teams) {
            if (team.active) {
                for (participant in team.participants) {
                    val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(participant.uuid)
                        ?: continue

                    cloudPlayer.connect(service)
                }
            }
        }
    }

    private fun startRound(count: Int) {
        val round = RoundPreparer(this, count).prepareRound()
        currentRound = round

        sendUnitPacket(PacketOutStartRound(this, round)).addCompleteListener {
            val starter = RoundStarter(this, round)
            starter.start()
            sendUnitPacket(PacketOutEncounterMatches(this))

            CloudAPI.instance.getEventManager().call(TournamentNextRoundEvent(this, round))
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
                return // TODO: Players who havent played yet should be prio
            }
        }

        val maxRounds = settings().maxRounds()
        if (currentRound.count == maxRounds) { // TODO: Send auto winners to tournament lobby
            notifyWinner(currentRound)
            setState(TournamentState.FINISHED)
        } else {
            broadcast(object : BroadcastMessage {
                override fun message(player: ICloudPlayer): String {
                    return "§aDie ${player.secondaryColor()}Turnier Runde §aist vorbei, die nächste (${currentRound.count + 1}) beginnt in Kürze."
                }
            })

            SCHEDULED_EXECUTOR.schedule({ startRound(currentRound.count + 1) }, 10, TimeUnit.SECONDS)
        }
    }

    private fun notifyWinner(currentRound: TournamentRound) {
        if (currentRound.encounters.size == 1) {
            winningTeam = currentRound.encounters[0].winnerTeam
        }

        val winner = if (winningTeam == null) {
            "§cNiemand hat gewonnen."
        } else {
            "§aTeam ${winningTeam!!.name()} hat gewonnen"
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

    companion object {
        val SCHEDULED_EXECUTOR: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

}
