package de.bytemc.tournaments.game

import de.bytemc.tournaments.api.TournamentMatchData
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.common.protocol.round.encounter.PacketOutWinEncounter
import de.bytemc.tournaments.game.listener.ServiceListener
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit

/**
 * @author Nico_ND1
 */
class TournamentGamePlugin : JavaPlugin(), Runnable {

    private var matchData: TournamentMatchData? = null
    private var isIngame = false
    private var task: BukkitTask? = null
    private val startTime = System.currentTimeMillis()
    private val timeSteps: ArrayList<TimeStep> = ArrayList()

    override fun onEnable() {
        val thisService = CloudPlugin.instance.thisService()
        val property: IProperty<TournamentMatchData>? = thisService.getProperty("tournamentMatch")
        if (property == null) {
            server.pluginManager.disablePlugin(this)
            return
        }

        matchData = property.getValue()
        CloudAPI.instance.getEventManager().registerListener(CloudPlugin.instance, ServiceListener(this))

        timeSteps.add(NotificatorTimeStep(TimeUnit.MINUTES.toMillis(1), 3))
        timeSteps.add(NotificatorTimeStep(TimeUnit.MINUTES.toMillis(2), 2))
        timeSteps.add(NotificatorTimeStep(TimeUnit.MINUTES.toMillis(3), 1))
        timeSteps.add(ShutdownTimeStep(TimeUnit.MINUTES.toMillis(4)))
        task = server.scheduler.runTaskTimer(this, this, 5 * 20, 5 * 20)
    }

    override fun run() {
        if (isIngame) {
            task?.cancel()
            return
        }

        val timeStep = timeSteps.firstOrNull { it.duration + startTime < System.currentTimeMillis() } ?: return
        timeSteps.remove(timeStep)
        timeStep.trigger()
    }

    fun notifyIngame() {
        isIngame = true
    }

    fun isTournamentMatch(): Boolean {
        return matchData != null
    }

    fun getMatchData(): TournamentMatchData? {
        return matchData
    }

    fun finish(vararg winners: Player) {
        for (winner in winners) {
            if (matchData!!.firstTeam.participants.any { par -> par.uuid == winner.uniqueId }) {
                finish(matchData!!.firstTeam)
                return
            } else if (matchData!!.secondTeam.participants.any { par -> par.uuid == winner.uniqueId }) {
                finish(matchData!!.secondTeam)
                return
            }
        }

        throw IllegalStateException("Couldn't find team for any winner of ${winners.contentToString()}")
    }

    fun finish(team: TournamentTeam) {
        val packet = PacketOutWinEncounter(matchData!!.tournamentID, matchData!!.roundID, matchData!!.encounterID, team)
        CloudPlugin.instance.communicationClient.getConnection().sendUnitQuery(packet)
    }

}
