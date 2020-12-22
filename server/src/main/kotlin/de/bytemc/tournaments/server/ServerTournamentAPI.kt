package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.MultiTeamsOptionReader
import de.bytemc.tournaments.server.protocol.PacketOutCreateTournament
import de.bytemc.tournaments.server.protocol.round.PacketOutStartRound
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
        games.add(TournamentGame("BedWars", "§c", "BedWars", teamsOptions))
    }

    fun createTournament(creator: TournamentCreator, settings: TournamentSettings): ServerTournament? {
        if (findTournamentByCreator(creator.uuid) != null) {
            return null
        }

        val id = UUID.randomUUID()
        val teams: ArrayList<TournamentTeam> = ArrayList(settings.teamsAmount)
        for (i in 0..settings.teamsAmount) {
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
            connection.sendUnitQuery(PacketOutCreateTournament(tournament))
            if (tournament.state() == TournamentState.PLAYING && tournament.currentRound() != null) {
                connection.sendUnitQuery(PacketOutStartRound(tournament, tournament.currentRound()!!))
            }
        }
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
            promise = listeningConnection.sendUnitQuery(packet).combine(promise, true)
        }
        return promise
    }

    fun addTournament(tournament: ServerTournament) {
        creationLock.withLock { tournaments.add(tournament) }
    }

    fun deleteTournament(tournament: ServerTournament) {
        creationLock.withLock { tournaments.remove(tournament) }
        tournament.delete()
    }

    init {
        instance = this
    }

    companion object {
        @JvmStatic
        lateinit var instance: ServerTournamentAPI
            private set
    }

    override fun allGames() = games

}
