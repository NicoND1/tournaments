package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.protocol.state.PacketOutSetState
import de.bytemc.tournaments.common.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.common.protocol.team.PacketOutRemoveTeamParticipant
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
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

    fun setState(newState: TournamentState): ICommunicationPromise<Boolean> {
        return sendPacket(PacketOutSetState(this, newState), Boolean::class.java)
    }

    fun addToTeam(player: Player, team: TournamentTeam): ICommunicationPromise<Boolean> {
        return sendPacket(PacketOutAddTeamParticipant(this, team, player.toParticipant()), Boolean::class.java)
    }

    fun removeFromTeam(player: Player, team: TournamentTeam): ICommunicationPromise<Boolean> {
        return sendPacket(PacketOutRemoveTeamParticipant(this, team, player.toParticipant()), Boolean::class.java)
    }

    fun sendPacket(packet: IPacket): ICommunicationPromise<Unit> {
        return LobbyTournamentAPI.instance.sendPacket(packet)
    }

    fun <T : Any> sendPacket(packet: IPacket, clazz: Class<T>): ICommunicationPromise<T> {
        return LobbyTournamentAPI.instance.sendPacket(packet, clazz)
    }

}
