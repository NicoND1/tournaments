package de.bytemc.tournaments.server

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
import de.bytemc.tournaments.server.command.TournamentDebugCommand
import de.bytemc.tournaments.server.listener.ServicesListener
import de.bytemc.tournaments.server.protocol.PacketInCreateTournament
import de.bytemc.tournaments.server.protocol.PacketInDeleteTournament
import de.bytemc.tournaments.server.protocol.PacketInStartListening
import de.bytemc.tournaments.server.protocol.lobby.PacketInSendToTournamentLobby
import de.bytemc.tournaments.server.protocol.round.PacketInStartRound
import de.bytemc.tournaments.server.protocol.round.encounter.PacketInEncounterMatches
import de.bytemc.tournaments.server.protocol.round.encounter.PacketInWinEncounter
import de.bytemc.tournaments.server.protocol.state.PacketInSetState
import de.bytemc.tournaments.server.protocol.team.PacketInAddTeamParticipant
import de.bytemc.tournaments.server.protocol.team.PacketInRemoveTeamParticipant
import de.bytemc.tournaments.server.protocol.team.PacketOutTeamMembers
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.launcher.startup.Launcher
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author Nico_ND1
 */
class TournamentsServerModule : ICloudModule {

    override fun onEnable() {
        CloudAPI.instance.getEventManager().registerListener(this, ServicesListener())

        registerPackets(CloudAPI.instance.getThisSidesCommunicationBootstrap().getPacketManager())
        //Launcher.instance.commandManager.registerCommand(this, TournamentDebugCommand())

        ServerTournamentAPI()

        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(TournamentDeletionRunnable(), 5L, 5L, TimeUnit.SECONDS)
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

        packetManager.registerPacket(PacketOutTeamMembers::class.java)

        packetManager.registerPacket(PacketOutSendToTournamentLobby::class.java)
        packetManager.registerPacket(PacketInSendToTournamentLobby::class.java)
    }

    override fun onDisable() {
    }
}
