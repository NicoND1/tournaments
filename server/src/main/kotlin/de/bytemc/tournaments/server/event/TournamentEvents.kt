package de.bytemc.tournaments.server.event

import de.bytemc.tournaments.api.TournamentRound
import de.bytemc.tournaments.server.ServerTournament
import eu.thesimplecloud.api.eventapi.IEvent

/**
 * @author Nico_ND1
 */
data class TournamentNextRoundEvent(val tournament: ServerTournament, val round: TournamentRound) : IEvent
data class TournamentStateChangeEvent(val tournament: ServerTournament) : IEvent
data class TournamentDeleteEvent(val tournament: ServerTournament) : IEvent
