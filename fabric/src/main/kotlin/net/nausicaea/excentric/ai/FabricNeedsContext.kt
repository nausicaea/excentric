package net.nausicaea.excentric.ai

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentUser
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.npc.InventoryCarrier
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.ai.service.Held
import net.nausicaea.excentric.ai.service.Inventory
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.ai.service.World
import net.nausicaea.excentric.fabric.api.lookup.ServiceApi
import net.nausicaea.excentric.minecraft.world.stream
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.andThen
import net.nausicaea.excentric.util.forEach
import java.util.stream.Stream

data class FabricNeedsContext(
    val needs: NeedsCollection,
    val entity: LivingEntity,
) : NeedsContext {
    override fun collect(): Stream<Service> {
        val level = entity.level()
        // The service collection is not designed to run on clients.
        if (level !is ServerLevel) {
            return Stream.empty()
        }

        val svcBuffer = ArrayList<Service>()

        // If the entity has an inventory.
        if (entity is InventoryCarrier) {
            val inventory = entity.inventory
            inventory
                .stream()
                .flatMap { stack ->
                    ServiceApi
                        .findItemService(
                            stack.value,
                            entity,
                            Inventory(inventory, stack.index),
                        ).toOptional()
                        .stream()
                }.forEach(svcBuffer::add)
        }

        // If the entity can carry equipment.
        if (entity is EquipmentUser) {
            Option
                .of(entity.getItemBySlot(EquipmentSlot.MAINHAND))
                .andThen { s ->
                    ServiceApi.findItemService(
                        s,
                        entity,
                        Held(EquipmentSlot.MAINHAND),
                    )
                }.forEach(svcBuffer::add)
            Option
                .of(entity.getItemBySlot(EquipmentSlot.OFFHAND))
                .andThen { s ->
                    ServiceApi.findItemService(
                        s,
                        entity,
                        Held(EquipmentSlot.OFFHAND),
                    )
                }.forEach(svcBuffer::add)
        }

        // Find item entities lying around somewhere in the world
        val pos = entity.position()
        val aabb = AABB(pos.add(SEARCH_MIN), pos.add(SEARCH_MAX))
        level
            .getEntitiesOfClass(ItemEntity::class.java, aabb)
            .stream()
            .flatMap { itemEntity ->
                ServiceApi
                    .findItemService(
                        itemEntity.item,
                        entity,
                        World(itemEntity),
                    ).toOptional()
                    .stream()
            }.forEach(svcBuffer::add)

        // TODO: Find services of items lying in containers somewhere.

        // TODO: Collect services from blocks in the world.

        // TODO: Collect services from other entities in the World.
        return svcBuffer.stream()
    }

    override fun needs(): NeedsCollection = needs

    override fun entity(): LivingEntity = entity

    companion object {
        private const val HALF_WIDTH = 5 shl 16
        private const val HALF_HEIGHT = 2 shl 16
        private val SEARCH_MIN = Vec3(-HALF_WIDTH.toDouble(), -HALF_HEIGHT.toDouble(), -HALF_WIDTH.toDouble())
        private val SEARCH_MAX = Vec3(HALF_WIDTH.toDouble(), HALF_HEIGHT.toDouble(), HALF_WIDTH.toDouble())
    }
}
