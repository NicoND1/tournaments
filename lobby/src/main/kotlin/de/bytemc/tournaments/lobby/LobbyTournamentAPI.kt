package de.bytemc.tournaments.lobby

import de.bytemc.tournaments.api.AbstractTournamentAPI
import de.bytemc.tournaments.api.TournamentGame
import de.bytemc.tournaments.common.MultiTeamsOptionReader
import java.util.concurrent.locks.ReentrantLock
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
        games.add(TournamentGame("BedWars", "§c", "BedWars", teamsOptions))
    }

    override fun allGames() = games

    fun addTournament(tournament: LobbyTournament) {
        creationLock.withLock { tournaments.add(tournament) }
    }

    fun deleteTournament(tournament: LobbyTournament) {
        creationLock.withLock { tournaments.remove(tournament) }

        // TODO: Close inventories for players who are looking at this tournament
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
