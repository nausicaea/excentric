package net.nausicaea.excentric.ai

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentUser
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.npc.InventoryCarrier
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.ai.service.ItemStackSource
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.fabric.api.lookup.ServiceApi
import net.nausicaea.excentric.minecraft.world.asSequence
import net.nausicaea.excentric.minecraft.world.entity.getNonemptyItemBySlot
import net.nausicaea.excentric.util.asOpt
import net.nausicaea.excentric.util.filterMap

data class FabricNeedsContext(
    val needs: NeedsCollection,
    val entity: LivingEntity,
) : NeedsContext {
    override fun collect(): Sequence<Service> {
        val level = entity.level()
        // The service collection is not designed to run on clients.
        if (level !is ServerLevel) {
            return emptySequence()
        }

        val svcBuffer = mutableListOf<Service>()

        // If the entity has an inventory.
        if (entity is InventoryCarrier) {
            val inventory = entity.inventory
            inventory
                .asSequence()
                .filterMap {
                    ServiceApi
                        .findItemService(
                            it.value,
                            entity,
                            ItemStackSource.Inventory(inventory, it.index),
                        )
                }.forEach(svcBuffer::add)
        }

        // If the entity can carry equipment.
        if (entity is EquipmentUser) {
            entity
                .getNonemptyItemBySlot(EquipmentSlot.MAINHAND)
                .andThen {
                    ServiceApi
                        .findItemService(
                            it,
                            entity,
                            ItemStackSource.Held(EquipmentSlot.MAINHAND),
                        ).asOpt()
                }.forEach(svcBuffer::add)
            entity
                .getNonemptyItemBySlot(EquipmentSlot.OFFHAND)
                .andThen {
                    ServiceApi
                        .findItemService(
                            it,
                            entity,
                            ItemStackSource.Held(EquipmentSlot.OFFHAND),
                        ).asOpt()
                }.forEach(svcBuffer::add)
        }

        // Find item entities lying around somewhere in the world
        val pos = entity.position()
        val aabb = AABB(pos.add(SEARCH_MIN), pos.add(SEARCH_MAX))
        level
            .getEntitiesOfClass(ItemEntity::class.java, aabb)
            .asSequence()
            .filterMap {
                ServiceApi
                    .findItemService(
                        it.item,
                        entity,
                        ItemStackSource.World(it),
                    )
            }.forEach(svcBuffer::add)

        // TODO: Find services of items lying in containers somewhere.

        // TODO: Collect services from blocks in the world.

        // TODO: Collect services from other entities in the World.
        return svcBuffer.asSequence()
    }

    override fun needs(): NeedsCollection = needs

    override fun entity(): LivingEntity = entity

    companion object {
        private const val HALF_WIDTH = 5 shl 4
        private const val HALF_HEIGHT = 2 shl 4
        private val SEARCH_MIN = Vec3(-HALF_WIDTH.toDouble(), -HALF_HEIGHT.toDouble(), -HALF_WIDTH.toDouble())
        private val SEARCH_MAX = Vec3(HALF_WIDTH.toDouble(), HALF_HEIGHT.toDouble(), HALF_WIDTH.toDouble())
    }
}
