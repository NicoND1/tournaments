package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.common.MultiTeamsOptionReader
import de.bytemc.tournaments.common.protocol.PacketOutCreateTournament
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.plugin.startup.CloudPlugin
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.concurrent.withLock

/**
 * @author Nico_ND1
 */
class LobbyTournamentAPI : AbstractTournamentAPI<LobbyTournament>() {

    private val creationLock = ReentrantLock()

    private val games: List<TournamentGame> = arrayListOf()

    init {
        games as ArrayList<TournamentGame>

        val teamsOptions = MultiTeamsOptionReader().readOptions("BedWars") // TODO: Load all games
        games.add(TournamentGame("BedWars", "§c", "BED", teamsOptions))
    }

    override fun allGames() = games

    fun createTournament(creator: TournamentCreator, settings: TournamentSettings): ICommunicationPromise<Boolean> {
        if (findTournamentByCreator(creator.uuid) != null) {
            return CommunicationPromise(result = false)
        }

        val id = UUID.randomUUID()
        val teams: ArrayList<TournamentTeam> = ArrayList(settings.teamsAmount)
        for (i in 0..settings.teamsAmount) {
            teams.add(TournamentTeam(i, arrayListOf()))
        }

        val packet = PacketOutCreateTournament(id, TournamentState.COLLECTING, creator, settings, teams)
        return sendPacket(packet, Boolean::class.java)
    }

    fun addTournament(tournament: LobbyTournament) {
        creationLock.withLock { tournaments.add(tournament) }
    }

    fun deleteTournament(tournament: ITournament) {
        creationLock.withLock { tournaments.remove(tournament) }

        // TODO: Close inventories for players who are looking at this tournament
    }

    fun sendPacket(packet: IPacket): ICommunicationPromise<Unit> {
        return CloudPlugin.instance.communicationClient.getConnection().sendUnitQuery(packet)
    }

    fun <T : Any> sendPacket(packet: IPacket, clazz: Class<T>): ICommunicationPromise<T> {
        return CloudPlugin.instance.communicationClient.getConnection().sendQuery(packet, clazz)
    }

    init {
        instance = this
    }

    companion object {
        @JvmStatic
        lateinit var instance: LobbyTournamentAPI
            private set
    }

}
