package de.bytemc.tournaments.game

import de.bytemc.tournaments.api.TournamentMatchData
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.common.protocol.round.encounter.PacketOutWinEncounter
import eu.thesimplecloud.api.property.IProperty
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nico_ND1
 */
class TournamentGamePlugin : JavaPlugin() {

    private var matchData: TournamentMatchData? = null

    override fun onEnable() {
        val thisService = CloudPlugin.instance.thisService()
        val property: IProperty<TournamentMatchData> = thisService.getProperty("tournamentMatch") ?: return

        matchData = property.getValue()
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
                break
            } else if (matchData!!.secondTeam.participants.any { par -> par.uuid == winner.uniqueId }) {
                finish(matchData!!.secondTeam)
                break
            }
        }

        throw IllegalStateException("Couldn't find team for any winner of $winners")
    }

    fun finish(team: TournamentTeam) {
        val packet = PacketOutWinEncounter(matchData!!.tournamentID, matchData!!.encounterID, team)
        CloudPlugin.instance.communicationClient.getConnection().sendUnitQuery(packet)
    }

}
