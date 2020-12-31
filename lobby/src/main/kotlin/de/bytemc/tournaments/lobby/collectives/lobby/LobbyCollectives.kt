package de.bytemc.tournaments.lobby.collectives.lobby

import de.bytemc.tournaments.api.TournamentEncounter
import de.bytemc.tournaments.api.TournamentTeam
import de.bytemc.tournaments.lobby.LobbyTournament
import de.bytemc.tournaments.lobby.collectives.ICollectives
import de.bytemc.tournaments.lobby.collectives.ICollectivesRepository
import java.util.*

/**
 * @author Nico_ND1
 */
class LobbyCollectives(private val repository: ICollectivesRepository) : ICollectives {
    override fun repository() = repository

    override fun handleRoundStart(tournament: LobbyTournament) {
    }

    override fun handleEncounterWin(encounter: TournamentEncounter) {
    }

    override fun handleDelete(id: UUID) {
    }

    override fun handleTeamUpdate(team: TournamentTeam) {
    }
}
