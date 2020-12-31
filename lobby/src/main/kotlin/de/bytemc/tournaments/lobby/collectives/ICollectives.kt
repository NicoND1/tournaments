package de.bytemc.tournaments.lobby.collectives

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import java.util.*

/**
 * @author Nico_ND1
 */
interface ICollectives {

    fun repository(): ICollectivesRepository

    fun handleRoundStart(tournament: LobbyTournament)

    fun handleEncounterWin(encounter: TournamentEncounter)

    fun handleDelete(id: UUID)

    fun handleTeamUpdate(team: TournamentTeam)

}
