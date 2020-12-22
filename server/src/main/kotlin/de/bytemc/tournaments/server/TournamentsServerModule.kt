package de.bytemc.tournaments.server

import de.bytemc.tournaments.server.listener.ServicesListener
import de.bytemc.tournaments.server.protocol.PacketInCreateTournament
import de.bytemc.tournaments.server.protocol.PacketInDeleteTournament
import de.bytemc.tournaments.server.protocol.PacketOutCreateTournament
import de.bytemc.tournaments.server.protocol.PacketOutDeleteTournament
import de.bytemc.tournaments.server.protocol.round.PacketOutStartRound
import de.bytemc.tournaments.server.protocol.round.encounter.PacketInWinEncounter
import de.bytemc.tournaments.server.protocol.round.encounter.PacketOutWinEncounter
import de.bytemc.tournaments.server.protocol.state.PacketInSetState
import de.bytemc.tournaments.server.protocol.state.PacketOutSetState
import de.bytemc.tournaments.server.protocol.team.PacketInAddTeamParticipant
import de.bytemc.tournaments.server.protocol.team.PacketInRemoveTeamParticipant
import de.bytemc.tournaments.server.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.server.protocol.team.PacketOutRemoveTeamParticipant
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.external.ICloudModule
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager

/**
 * @author Nico_ND1
 */
class TournamentsServerModule : ICloudModule {

    override fun onEnable() {
        CloudAPI.instance.getEventManager().registerListener(this, ServicesListener())

        registerPackets(CloudAPI.instance.getThisSidesCommunicationBootstrap().getPacketManager())
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

        packetManager.registerPacket(PacketInWinEncounter::class.java)
        packetManager.registerPacket(PacketOutWinEncounter::class.java)

        packetManager.registerPacket(PacketOutDeleteTournament::class.java)
        packetManager.registerPacket(PacketInDeleteTournament::class.java)
    }

    override fun onDisable() {
    }
}
