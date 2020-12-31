package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.MultiTeamsOptionReader
import de.bytemc.tournaments.common.protocol.PacketOutCreateTournament
import de.bytemc.tournaments.common.protocol.PacketOutDeleteTournament
import de.bytemc.tournaments.common.protocol.round.PacketOutStartRound
import de.bytemc.tournaments.server.event.TournamentDeleteEvent
import de.bytemc.tournaments.server.protocol.team.PacketOutTeamMembers
import eu.thesimplecloud.api.CloudAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class ServerTournamentAPI : AbstractTournamentAPI<ServerTournament>() {

    private val creationLock = ReentrantLock()
    private val listeningLock = ReentrantLock()

    private val listeningConnections: ArrayList<IConnection> = arrayListOf()

    private val games: List<TournamentGame> = arrayListOf()

    init {
        games as ArrayList<TournamentGame>

        val teamsOptions = MultiTeamsOptionReader().readOptions("BedWars") // TODO: Load all games
        games.add(TournamentGame("BedWars", "§c", "BED", teamsOptions))
    }

    override fun allGames() = games

    fun createTournament(creator: TournamentCreator, settings: TournamentSettings): ServerTournament? {
        if (findTournamentByCreator(creator.uuid) != null) {
            return null
        }

        val id = UUID.randomUUID()
        val teams: ArrayList<TournamentTeam> = ArrayList(settings.teamsAmount)
        for (i in 1..settings.teamsAmount) {
            teams.add(TournamentTeam(i, arrayListOf()))
        }

        val tournament = ServerTournament(id, creator, settings, teams)
        creationLock.withLock { tournaments.add(tournament) }
        sendUnitPacket(PacketOutCreateTournament(tournament))
        return tournament
    }

    fun startListening(connection: IConnection) {
        listeningLock.withLock { listeningConnections.add(connection) }

        for (tournament in tournaments) {
            val packets: ArrayList<IPacket> = ArrayList()
            packets.add(PacketOutCreateTournament(tournament))
            if (tournament.teams().any { team -> team.participants.isNotEmpty() }) {
                packets.add(PacketOutTeamMembers(tournament))
            }
            if (tournament.state() == TournamentState.PLAYING && tournament.currentRound() != null) {
                packets.add(PacketOutStartRound(tournament, tournament.currentRound()!!))
            }

            send(connection, packets)
        }
    }

    private fun send(connection: IConnection, packets: ArrayList<IPacket>) {
        if (packets.isEmpty()) return
        connection.sendUnitQuery(packets.removeAt(0)).addCompleteListener { send(connection, packets) }
    }

    fun stopListening(connection: IConnection) {
        listeningLock.withLock { listeningConnections.remove(connection) }
    }

    fun sendUnitPacket(packet: IPacket): ICommunicationPromise<Unit> {
        return sendUnitPacket(packet, listeningConnections)
    }

    fun sendUnitPacket(packet: IPacket, connectionToIgnore: IConnection): ICommunicationPromise<Unit> {
        val list = ArrayList(listeningConnections)
        list.remove(connectionToIgnore)
        return sendUnitPacket(packet, list)
    }

    fun sendUnitPacket(packet: IPacket, connections: List<IConnection>): ICommunicationPromise<Unit> {
        var promise: ICommunicationPromise<Unit> = CommunicationPromise(0L, true)
        for (listeningConnection in connections) {
            if (listeningConnection.isOpen()) {
                promise = listeningConnection.sendUnitQuery(packet).combine(promise, true)
            }
        }
        return promise
    }

    fun addTournament(tournament: ServerTournament) {
        creationLock.withLock { tournaments.add(tournament) }
    }

    fun deleteTournament(tournament: ServerTournament) {
        creationLock.withLock { tournaments.remove(tournament) }
        tournament.delete()
        sendUnitPacket(PacketOutDeleteTournament(tournament.id()))

        CloudAPI.instance.getEventManager().call(TournamentDeleteEvent(tournament))
    }

    override fun toString(): String {
        return "ServerTournamentAPI(listeningConnections=$listeningConnections, games=$games, tournaments=$tournaments)"
    }

    init {
        instance = this
    }

    companion object {
        @JvmStatic
        lateinit var instance: ServerTournamentAPI
            private set
    }

}
