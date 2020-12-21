package de.bytemc.tournaments.server.protocol

import de.bytemc.tournaments.api.TournamentCreator
import de.bytemc.tournaments.api.TournamentSettings
import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.server.ServerTournament
import de.bytemc.tournaments.server.ServerTournamentAPI
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*

/**
 * @author Nico_ND1
 */
class PacketInCreateTournament : JsonPacket() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val id = jsonLib.getObject("id", UUID::class.java)
        val state = jsonLib.getObject("state", TournamentState::class.java)
        val creator = jsonLib.getObject("creator", TournamentCreator::class.java)
        val settings = jsonLib.getObject("settings", TournamentSettings::class.java)

        val teamList: List<TournamentTeam> = arrayListOf()
        val teams = jsonLib.getObject("teams", teamList::class.java)

        addTournament(id!!, state!!, creator!!, settings!!, teams!!)

        return unit()
    }

    private fun addTournament(
        id: UUID,
        state: TournamentState,
        creator: TournamentCreator,
        settings: TournamentSettings,
        teams: List<TournamentTeam>
    ) {
        val tournament = ServerTournament(id, creator, settings, teams)
        tournament.currentState = state

        ServerTournamentAPI.instance.addTournament(tournament)
    }
}
