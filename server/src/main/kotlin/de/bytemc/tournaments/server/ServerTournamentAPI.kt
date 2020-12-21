package de.bytemc.tournaments.server

import de.bytemc.tournaments.api.AbstractTournamentAPI
import de.bytemc.tournaments.api.TournamentCreator
import de.bytemc.tournaments.server.protocol.PacketOutCreateTournament
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class ServerTournamentAPI : AbstractTournamentAPI<ServerTournament>() {

    private val creationLock = ReentrantLock()
    private val listeningLock = ReentrantLock()

    private val listeningConnections: ArrayList<IConnection> = arrayListOf()

    fun createTournament(creator: TournamentCreator): ServerTournament? {
        TODO()
    }

    fun startListening(connection: IConnection) {
        listeningLock.withLock { listeningConnections.add(connection) }

        for (tournament in tournaments) {
            connection.sendUnitQuery(PacketOutCreateTournament(tournament))
        }
        TODO("Send all tournaments")
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

    private fun sendUnitPacket(packet: IPacket, connections: List<IConnection>): ICommunicationPromise<Unit> {
        var promise: ICommunicationPromise<Unit> = CommunicationPromise(0L, true)
        for (listeningConnection in connections) {
            promise = listeningConnection.sendUnitQuery(packet).combine(promise, true)
        }
        return promise
    }

    fun addTournament(tournament: ServerTournament) {
        tournaments.add(tournament)
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

fun ServerTournament.sendUnitPacket(packet: IPacket): ICommunicationPromise<Unit> {
    return sendUnitPacket(packet)
}

fun ServerTournament.sendUnitPacket(packet: IPacket, connectionToIgnore: IConnection): ICommunicationPromise<Unit> {
    return sendUnitPacket(packet, connectionToIgnore)
}
