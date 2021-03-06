package de.bytemc.tournaments.api

import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.jsonlib.JsonLibExclude
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow

/**
 * @author Nico_ND1
 */
data class TournamentCreator(val uuid: UUID, val name: String)

data class TournamentTeam(val id: Int, val participants: ArrayList<TournamentParticipant>, var active: Boolean = true) {
    @JsonLibExclude
    val participantsLock = ReentrantLock()

    fun isEmpty() = participants.isEmpty()

    fun name(): String {
        return participants.stream().map { participant -> participant.name }.collect(Collectors.joining(", "))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TournamentTeam
        if (id != other.id) return false
        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "TournamentTeam(id=$id, participants=$participants"
    }

}

data class TournamentParticipant(val uuid: UUID, val name: String, val texture: PlayerTexture? = null) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TournamentParticipant
        if (uuid != other.uuid) return false
        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }

    override fun toString(): String {
        return "TournamentParticipant(uuid=$uuid, name='$name')"
    }
}

data class PlayerTexture(val value: String, val signature: String)

data class TournamentSettings(
    val game: TournamentGame,
    val maps: List<TournamentMap>,
    val teamsOption: TournamentTeamsOption,
    val teamsAmount: Int,
) {
    fun maxRounds(): Int {
        return ceil(log10(teamsAmount.toDouble()) / log10(2.toDouble())).toInt()
    }

    fun matchCount(roundCount: Int): Int {
        return (teamsAmount / 2.0.pow((roundCount).toDouble())).toInt()
    }
}

data class TournamentGame(
    val name: String,
    val color: String,
    val materialName: String,
    val teamsOptions: ArrayList<TournamentTeamsOption>,
)

data class TournamentTeamsOption(
    val playersPerTeam: Int,
    val mapPool: ArrayList<TournamentMap>,
    val serviceGroupName: String,
)

data class TournamentMap(val name: String)

enum class TournamentState {
    COLLECTING,
    PLAYING,
    FINISHED
}

data class TournamentRound(
    val count: Int,
    val encounters: Array<TournamentEncounter>,
    var parentRound: TournamentRound? = null,
) {
    fun findEncounter(id: Int): TournamentEncounter? {
        return encounters.firstOrNull { encounter -> encounter.id == id }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TournamentRound
        if (count != other.count) return false
        return true
    }

    override fun hashCode() = count
}

data class TournamentEncounter(
    val id: Int,
    val firstTeam: TournamentTeam,
    val secondTeam: TournamentTeam,
    var winnerTeam: TournamentTeam? = null,
    var serviceID: UUID? = null,
)

fun BytePacket.writeUUID(uuid: UUID) {
    buffer.writeLong(uuid.mostSignificantBits)
    buffer.writeLong(uuid.leastSignificantBits)
}

fun BytePacket.readUUID(): UUID {
    return UUID(buffer.readLong(), buffer.readLong())
}

fun BytePacket.writeString(string: String) {
    buffer.writeInt(string.length)
    buffer.writeBytes(string.toByteArray(StandardCharsets.UTF_8))
}

fun BytePacket.readString(): String {
    val length = buffer.readInt()
    return buffer.readBytes(length).toString(StandardCharsets.UTF_8)
}
