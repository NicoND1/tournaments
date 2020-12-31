package de.bytemc.tournaments.lobby.collectives

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleCreateEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.StructureGrowEvent
import org.bukkit.event.world.WorldUnloadEvent

/**
 * @author Nico_ND1
 */
class CancelListener : Listener {

    @EventHandler
    fun handle(event: FoodLevelChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerInteractEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerFishEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerPortalEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerDeathEvent) {
        event.deathMessage = null
        event.keepInventory = true
    }

    @EventHandler
    fun handle(event: PlayerAchievementAwardedEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerBedEnterEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: InventoryClickEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerPickupItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerDropItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerEditBookEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerEggThrowEvent) {
        event.egg.remove()
    }

    @EventHandler
    fun handle(event: PlayerExpChangeEvent) {
        event.amount = 0
    }

    @EventHandler
    fun handle(event: PlayerItemConsumeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerItemDamageEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerShearEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerStatisticIncrementEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerLeashEntityEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PlayerArmorStandManipulateEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: WorldUnloadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: ChunkUnloadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: WeatherChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityCombustEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityDamageEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityExplodeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockExplodeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: FireworkExplodeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityPortalEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityInteractEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityTargetEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityTameEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntitySpawnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityBreakDoorEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityChangeBlockEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityRegainHealthEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityCreatePortalEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EntityShootBowEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockBreakEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockPlaceEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockPhysicsEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockDispenseEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockRedstoneEvent) {
        event.newCurrent = 0
    }

    @EventHandler
    fun handle(event: BlockExpEvent) {
        event.expToDrop = 0
    }

    @EventHandler
    fun handle(event: BlockFormEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockDamageEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockFadeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockGrowEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockIgniteEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockPistonExtendEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockPistonRetractEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: BlockSpreadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: PrepareItemEnchantEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: EnchantItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: HangingBreakEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: HangingPlaceEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: StructureGrowEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: VehicleCreateEvent) {
        event.vehicle.remove()
    }

    @EventHandler
    fun handle(event: VehicleEntityCollisionEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: VehicleDestroyEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun handle(event: VehicleEnterEvent) {
        event.isCancelled = true
    }

}
