package de.bytemc.tournaments.lobby.protocol

import de.bytemc.tournaments.api.*
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.LobbyTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketInCreateTournament : BytePacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<BooleanResult> {
        val id = readUUID()
        val creator = TournamentCreator(readUUID(), readString())
        if (LobbyTournamentAPI.instance.findTournamentByCreator(creator.uuid) != null) {
            return success(BooleanResult.FALSE)
        }

        val settings = findSettings()

        val teams: ArrayList<TournamentTeam> = arrayListOf()
        for (i in 0..settings.teamsAmount) {
            teams.add(TournamentTeam(i, arrayListOf()))
        }

        val tournament = LobbyTournament(id, creator, settings, teams)
        LobbyTournamentAPI.instance.addTournament(tournament)
        return success(BooleanResult.TRUE)
    }

    private fun findSettings(): TournamentSettings {
        val gameName = readString()
        val game = LobbyTournamentAPI.instance.allGames().firstOrNull { game -> game.name == gameName }
            ?: throw NullPointerException("Game coulnd't be found for $gameName")

        val playersPerTeam = buffer.readInt()
        val teamsOption = game.teamsOptions.firstOrNull { option -> option.playersPerTeam == playersPerTeam }
            ?: throw NullPointerException("Teams option coulnd't be found for $gameName")
        val teamsAmount = buffer.readInt()

        val maps: ArrayList<TournamentMap> = arrayListOf()
        val mapSize = buffer.readInt()
        for (i in 0..mapSize) {
            val mapName = readString()

            val map = teamsOption.mapPool.find { map -> map.name == mapName }
            if (map != null) maps.add(map)
        }

        return TournamentSettings(game, maps, teamsOption, teamsAmount)
    }
}
