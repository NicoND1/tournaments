package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.common.protocol.PacketOutCreateTournament
import de.bytemc.tournaments.common.protocol.PacketOutDeleteTournament
import de.bytemc.tournaments.common.protocol.PacketOutStartListening
import de.bytemc.tournaments.common.protocol.lobby.PacketOutSendToTournamentLobby
import de.bytemc.tournaments.common.protocol.round.PacketOutStartRound
import de.bytemc.tournaments.common.protocol.round.encounter.PacketOutEncounterMatches
import de.bytemc.tournaments.common.protocol.round.encounter.PacketOutWinEncounter
import de.bytemc.tournaments.common.protocol.state.PacketOutSetState
import de.bytemc.tournaments.common.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.common.protocol.team.PacketOutRemoveTeamParticipant
import de.bytemc.tournaments.lobby.collectives.*
import de.bytemc.tournaments.lobby.collectives.lobby.LobbyCollectives
import de.bytemc.tournaments.lobby.collectives.lobby.LobbyCollectivesRepository
import de.bytemc.tournaments.lobby.collectives.player.ActionBarRunnable
import de.bytemc.tournaments.lobby.command.TournamentCommand
import de.bytemc.tournaments.lobby.listener.JoinListener
import de.bytemc.tournaments.lobby.protocol.PacketInCreateTournament
import de.bytemc.tournaments.lobby.protocol.PacketInDeleteTournament
import de.bytemc.tournaments.lobby.protocol.PacketInStartListening
import de.bytemc.tournaments.lobby.protocol.lobby.PacketInSendToTournamentLobby
import de.bytemc.tournaments.lobby.protocol.round.PacketInStartRound
import de.bytemc.tournaments.lobby.protocol.round.encounter.PacketInEncounterMatches
import de.bytemc.tournaments.lobby.protocol.round.encounter.PacketInWinEncounter
import de.bytemc.tournaments.lobby.protocol.state.PacketInSetState
import de.bytemc.tournaments.lobby.protocol.team.PacketInAddTeamParticipant
import de.bytemc.tournaments.lobby.protocol.team.PacketInRemoveTeamParticipant
import de.bytemc.tournaments.lobby.protocol.team.PacketInTeamMembers
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.plugin.startup.CloudPlugin
import org.bukkit.plugin.java.JavaPlugin

/**
 * @author Nico_ND1
 */
class TournamentLobbyPlugin : JavaPlugin() {

    var collectives: ICollectives = LobbyCollectives(LobbyCollectivesRepository())

    override fun onEnable() {
        registerPackets(CloudAPI.instance.getThisSidesCommunicationBootstrap().getPacketManager())

        val api = LobbyTournamentAPI()
        api.sendPacket(PacketOutStartListening())

        server.pluginManager.registerEvents(JoinListener(), this)

        if (isCollectivesService()) {
            val repository = CollectivesPlayerRepository()
            collectives = CollectivesImpl(repository)
            server.pluginManager.registerEvents(CollectivesListener(repository), this)
            server.pluginManager.registerEvents(CancelListener(), this)
            server.scheduler.runTaskTimerAsynchronously(this, ActionBarRunnable(repository), 2 * 20, 2 * 20)
        } else {
            getCommand("tournament").executor = TournamentCommand()
        }
    }

    private fun isCollectivesService(): Boolean {
        return !CloudPlugin.instance.thisService().isLobby()
    }

    private fun registerPackets(packetManager: IPacketManager) {
        packetManager.registerPacket(PacketOutCreateTournament::class.java)
        packetManager.registerPacket(PacketInCreateTournament::class.java)

        packetManager.registerPacket(PacketOutAddTeamParticipant::class.java)
        packetManager.registerPacket(PacketInAddTeamParticipant::class.java)

        packetManager.registerPacket(PacketOutRemoveTeamParticipant::class.java)
        packetManager.registerPacket(PacketInRemoveTeamParticipant::class.java)

        packetManager.registerPacket(PacketOutSetState::class.java)
        packetManager.registerPacket(PacketInSetState::class.java)

        packetManager.registerPacket(PacketOutStartRound::class.java)
        packetManager.registerPacket(PacketInStartRound::class.java)

        packetManager.registerPacket(PacketInWinEncounter::class.java)
        packetManager.registerPacket(PacketOutWinEncounter::class.java)

        packetManager.registerPacket(PacketOutDeleteTournament::class.java)
        packetManager.registerPacket(PacketInDeleteTournament::class.java)

        packetManager.registerPacket(PacketOutStartListening::class.java)
        packetManager.registerPacket(PacketInStartListening::class.java)

        packetManager.registerPacket(PacketOutEncounterMatches::class.java)
        packetManager.registerPacket(PacketInEncounterMatches::class.java)

        packetManager.registerPacket(PacketInTeamMembers::class.java)

        packetManager.registerPacket(PacketOutSendToTournamentLobby::class.java)
        packetManager.registerPacket(PacketInSendToTournamentLobby::class.java)
    }

}
