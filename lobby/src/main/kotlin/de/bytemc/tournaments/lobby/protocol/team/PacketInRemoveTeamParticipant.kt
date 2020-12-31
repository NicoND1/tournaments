package de.bytemc.tournaments.lobby.protocol.team

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import de.bytemc.tournaments.lobby.TournamentLobbyPlugin
import de.bytemc.tournaments.lobby.inventory.TeamsInventory
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class PacketInRemoveTeamParticipant : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<BooleanResult> {
        val id = readUUID()
        val tournament = LobbyTournamentAPI.instance.findTournament(id)

        if (tournament != null) {
            return success(removeFromTournament(tournament))
        }
        return success(BooleanResult.FALSE)
    }

    private fun removeFromTournament(tournament: ITournament): BooleanResult {
        val teamID = buffer.readInt()
        for (team in tournament.teams()) {
            if (team.id == teamID) {
                return removeFromTeam(team)
            }
        }

        return BooleanResult.FALSE
    }

    private fun removeFromTeam(team: TournamentTeam): BooleanResult {
        val participantID = readUUID()

        var participant: TournamentParticipant? = null
        for (participants in team.participants) {
            if (participants.uuid == participantID) {
                participant = participants
            }
        }

        if (participant == null) {
            return BooleanResult.FALSE
        } else {
            team.participantsLock.withLock { team.participants.remove(participant) }
            updateInventories(team)
        }
        return BooleanResult.TRUE
    }

    private fun updateInventories(team: TournamentTeam) {
        val plugin = JavaPlugin.getPlugin(TournamentLobbyPlugin::class.java)
        plugin.collectives.handleTeamUpdate(team)

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            ClickInventory.getClickInventory(onlinePlayer.uniqueId).ifPresent {
                if (it is TeamsInventory) {
                    for (value in it.values) {
                        if (value.value.id == team.id) {
                            it.updateItemAt(value.key, team)
                        }
                    }
                }
            }
        }
    }

}
