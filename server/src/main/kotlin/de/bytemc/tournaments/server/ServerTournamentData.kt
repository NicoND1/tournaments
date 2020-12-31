package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.common.broadcast.BroadcastMessage
import de.bytemc.tournaments.common.broadcast.primaryColor
import de.bytemc.tournaments.common.broadcast.secondaryColor
import de.bytemc.tournaments.common.protocol.round.encounter.PacketOutWinEncounter
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.api.player.ICloudPlayer

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

fun TournamentEncounter.setWinnerTeam(tournament: ServerTournament, winnerTeam: TournamentTeam) {
    if (this.winnerTeam != null) return
    this.winnerTeam = winnerTeam

    tournament.sendUnitPacket(PacketOutWinEncounter(tournament.id(),
        tournament.currentRound()?.count ?: 0,
        this.id,
        winnerTeam))
    tournament.testRoundOver()
    tournament.moveToLobby(firstTeam, secondTeam)
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
