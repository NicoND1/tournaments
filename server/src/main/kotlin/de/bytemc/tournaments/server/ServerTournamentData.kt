package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.broadcast.BroadcastMessage
import de.bytemc.tournaments.server.broadcast.primaryColor
import de.bytemc.tournaments.server.broadcast.secondaryColor
import de.bytemc.tournaments.server.protocol.round.encounter.PacketOutWinEncounter
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * @author Nico_ND1
 */
fun TournamentEncounter.broadcast(message: BroadcastMessage) {
    firstTeam.broadcast(message)
    secondTeam.broadcast(message)
}

fun TournamentTeam.broadcast(message: BroadcastMessage) {
    for (participant in participants) {
        val cloudPlayer = CloudAPI.instance.getCloudPlayerManager().getCachedCloudPlayer(participant.uuid)

        if (cloudPlayer != null && message.canReceive(cloudPlayer)) {
            cloudPlayer.sendMessage(message.message(cloudPlayer))
        }
    }
}

fun BytePacket.writeUUID(uuid: UUID) {
    buffer.writeLong(uuid.mostSignificantBits)
    buffer.writeLong(uuid.leastSignificantBits)
}

fun BytePacket.readUUID(): UUID {
    return UUID(buffer.readLong(), buffer.readLong())
}

fun BytePacket.writeString(string: String) {
    buffer.writeInt(string.length)
    buffer.writeBytes(string.toByteArray(StandardCharsets.UTF_8))
}

fun BytePacket.readString(): String {
    val length = buffer.readInt()
    return buffer.readBytes(length).toString(StandardCharsets.UTF_8)
}

fun TournamentEncounter.setWinnerTeam(tournament: ServerTournament, winnerTeam: TournamentTeam) {
    this.winnerTeam = winnerTeam

    tournament.sendUnitPacket(PacketOutWinEncounter(tournament, this, winnerTeam))
    tournament.testRoundOver()
}

fun TournamentEncounter.handleError(tournament: ServerTournament) {
    setWinnerTeam(tournament, firstTeam)
    broadcast(object : BroadcastMessage {
        override fun message(player: ICloudPlayer): String {
            return "§cEuer Server für die ${player.secondaryColor()}Turnier Runde §ckonnte nicht gestartet werden.\n" +
                    " §cDamit weiter gespielt werden kann, gewinnt ${player.primaryColor()}Team ${firstTeam.name()} §cautomatisch."
        }
    })
}
