package de.bytemc.tournaments.lobby.inventory

import de.bytemc.core.ByteAPI
import de.bytemc.core.entitiesutils.inventories.ClickInventory
import de.bytemc.core.entitiesutils.inventories.ClickResult
import de.bytemc.core.entitiesutils.inventories.ClickableItem
import de.bytemc.core.entitiesutils.inventories.NoneClickableItem
import de.bytemc.core.entitiesutils.items.ItemCreator
import de.bytemc.tournaments.api.BooleanResult
import de.bytemc.tournaments.api.ITournament
import de.bytemc.tournaments.api.TournamentState
import de.bytemc.tournaments.common.protocol.PacketOutDeleteTournament
import de.bytemc.tournaments.common.protocol.team.PacketOutAddTeamParticipant
import de.bytemc.tournaments.common.protocol.team.PacketOutRemoveTeamParticipant
import de.bytemc.tournaments.lobby.*
import de.bytemc.tournaments.lobby.inventory.pairing.PairingInventory
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.TimeUnit

/**
 * @author Nico_ND1
 */
class ManageInventory(val player: Player, val tournament: LobbyTournament) :
    ClickInventory(3 * 9, player.format("Turnier von ${player.primaryColor()}${tournament.creator().name}")),
    ITournamentInventory {

    private var participationCooldown: Long = 0L

    init {
        setItems()
    }

    private fun setItems() {
        design(player, 22, 0, 2)

        setTeamsItem()
        setParticipationItem()
        setPairingItem()

        if (tournament.creator().uuid == player.uniqueId) {
            setDeletionItem()
            setActionItem()
        }
    }

    override fun openSilent(player: Player?) {
        setItems()
        super.openSilent(player)
    }

    companion object {
        val PARTICIPATION_COOLDOWN = TimeUnit.SECONDS.toMillis(2)
        private val PLAY_HEAD = ItemCreator("fa8f6b131ef847d9160e516a6f44bfa932554d40c18a81796d766a5487b9f710")
        private val PLAYING_HEAD = ItemCreator("cecd041f628c005a690fc6b8237e7311bb7c3b3aac10539fefe396a4c7c783e7")
    }

    private fun setTeamsItem() {
        setItem(11, object : ClickableItem(ItemCreator(Material.BED).setName(player.format("Teams")).toItemStack()) {
            override fun onClick(p0: Player, p1: ItemStack): ClickResult {
                TeamsInventory(player, tournament).open(player)
                return ClickResult.CANCEL
            }
        })
    }

    private fun setDeletionItem() {
        setItem(14, object :
            ClickableItem(ItemCreator(Material.BARRIER).setName(player.format("§cTurnier löschen")).toItemStack()) {
            override fun onClick(p0: Player, p1: ItemStack): ClickResult {
                LobbyTournamentAPI.instance.deleteTournament(tournament)
                tournament.sendPacket(PacketOutDeleteTournament(tournament.id()))
                clear()
                p0.updateInventory()
                return ClickResult.CANCEL
            }
        })
    }

    private fun setActionItem() {
        if (tournament.state() == TournamentState.COLLECTING) {
            setItem(15, object : ClickableItem(PLAY_HEAD.setName(player.format("Turnier starten")).toItemStack()) {
                override fun onClick(p0: Player, p1: ItemStack): ClickResult {
                    tournament.setState(TournamentState.PLAYING).addResultListener {

                    }.throwFailure()
                    return ClickResult.CANCEL
                }
            })
        } else {
            val item = PLAYING_HEAD.setName(player.format("§cTurnier ist bereits gestartet")).toItemStack()
            setItem(15, NoneClickableItem(item))
        }
    }

    private fun setPairingItem() {
        setItem(13, object :
            ClickableItem(ItemCreator(Material.GOLD_HELMET).setName(player.format("Paarungen")).toItemStack()) {
            override fun onClick(player: Player, itemStack: ItemStack): ClickResult {
                if (tournament.state() == TournamentState.COLLECTING) {
                    val bytePlayer = ByteAPI.getInstance().bytePlayerManager.players[player.uniqueId]
                    if (bytePlayer != null) player.sendMessage(bytePlayer.getPrefix("Lobby") + "Das Tunier hat noch nicht Angefangen§8.")
                } else {
                    PairingInventory(tournament,
                        ByteAPI.getInstance().bytePlayerManager.players[player.uniqueId]!!).open(player)
                }
                return ClickResult.CANCEL
            }
        })
    }

    private fun setParticipationItem() {
        setItem(12, object : ClickableItem(getParticipationItem()) {
            override fun onClick(p0: Player, p1: ItemStack): ClickResult {


                val bytePlayer = ByteAPI.getInstance().bytePlayerManager.players[player.uniqueId]
                val p0Byte = ByteAPI.getInstance().bytePlayerManager.players[p0.uniqueId]


                if (tournament.state() != TournamentState.COLLECTING) {
                    if (p0Byte != null) p0.sendMessage(p0Byte.getPrefix("Lobby") + "§7Das Turnier ist nicht betretbar§8.")
                } else {
                    if (participationCooldown > System.currentTimeMillis()) {
                        if (bytePlayer != null) player.sendMessage(bytePlayer.getPrefix("Lobby") + "Bitte warte einen moment§8...")
                        player.playSound(player.location, Sound.VILLAGER_NO, 1f, 1f)
                        return ClickResult.CANCEL
                    }
                    participationCooldown = System.currentTimeMillis() + PARTICIPATION_COOLDOWN

                    val team = tournament.findTeam(p0.uniqueId)
                    val packet = if (team == null) {
                        val freeTeam = tournament.teams().firstOrNull { aTeam -> aTeam.isEmpty() }
                        if (freeTeam == null) {
                            if (bytePlayer != null) player.sendMessage(bytePlayer.getPrefix("Lobby") + "§7Kein freies Team gefunden§8.")
                            return ClickResult.CANCEL
                        }

                        PacketOutAddTeamParticipant(tournament, freeTeam, player.toParticipant())
                    } else {
                        PacketOutRemoveTeamParticipant(tournament, team, player.toParticipant())
                    }

                    LobbyTournamentAPI.instance.sendPacket(packet, BooleanResult::class.java).addResultListener {
                        if (it.result) {
                            setParticipationItem()
                            player.updateInventory()
                        } else {
                            player.playSound(player.location, Sound.VILLAGER_NO, 1f, 1f)
                        }
                    }.throwFailure()
                }

                return ClickResult.CANCEL
            }
        })
    }

    private fun getParticipationItem(): ItemStack {
        var material = Material.POWERED_MINECART
        var text = "§cTurnier nicht betretbar"
        if (tournament.state() == TournamentState.COLLECTING) {
            if (tournament.findTeam(player.uniqueId) == null) {
                if (tournament.isFull()) {
                    text = "§cTurnier ist voll"
                } else {
                    material = Material.MINECART
                    text = "Turnier beitreten"
                }
            } else {
                material = Material.STORAGE_MINECART
                text = "Turnier austreten"
            }
        }

        return ItemCreator(material).setName(player.format(text)).toItemStack()
    }

    override fun getTournament(): ITournament {
        return tournament
    }

}
