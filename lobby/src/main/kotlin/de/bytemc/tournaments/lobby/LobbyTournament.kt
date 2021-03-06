package de.bytemc.tournaments.lobby

import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.protocol.state.PacketOutSetState
import de.bytemc.tournaments.common.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.common.protocol.team.PacketOutRemoveTeamParticipant
import de.bytemc.tournaments.lobby.inventory.IUpdatingTournamentInventory
import de.bytemc.tournaments.lobby.inventory.pairing.TournamentPairings
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

/**
 * @author Nico_ND1
 */
class LobbyTournament(
    id: UUID,
    creator: TournamentCreator,
    settings: TournamentSettings,
    teams: List<TournamentTeam>,
) : AbstractTournament(id, creator, settings, teams) {

    var pairings: TournamentPairings? = null

    fun setState(newState: TournamentState): ICommunicationPromise<BooleanResult> {
        return sendPacket(PacketOutSetState(this, newState), BooleanResult::class.java)
    }

    fun addToTeam(player: Player, team: TournamentTeam): ICommunicationPromise<BooleanResult> {
        return sendPacket(PacketOutAddTeamParticipant(this, team, player.toParticipant()), BooleanResult::class.java)
    }

    fun removeFromTeam(player: Player, team: TournamentTeam): ICommunicationPromise<BooleanResult> {
        return sendPacket(PacketOutRemoveTeamParticipant(this, team, player.toParticipant()), BooleanResult::class.java)
    }

    fun sendPacket(packet: IPacket): ICommunicationPromise<Unit> {
        return LobbyTournamentAPI.instance.sendPacket(packet)
    }

    fun <T : Any> sendPacket(packet: IPacket, clazz: Class<T>): ICommunicationPromise<T> {
        return LobbyTournamentAPI.instance.sendPacket(packet, clazz)
    }

    fun updateAllInventories() {
        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            ClickInventory.getClickInventory(onlinePlayer.uniqueId).ifPresent {
                if (it is IUpdatingTournamentInventory) {
                    if (it.getTournament().id() == id()) {
                        it.updateItems()
                    }
                }
            }
        }
    }

}
