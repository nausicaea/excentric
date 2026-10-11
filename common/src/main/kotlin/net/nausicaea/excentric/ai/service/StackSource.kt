package net.nausicaea.excentric.ai.service

import net.minecraft.world.Container
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.phys.Vec3

sealed interface StackSource {
    data class Held(
        val slot: EquipmentSlot,
    ) : StackSource

    data class Inventory(
        val container: Container,
        val slot: Int,
    ) : StackSource

    data class ForeignInventory(
        val container: Container,
        val slot: Int,
        val pos: Vec3,
    ) : StackSource

    data class World(
        val entity: ItemEntity,
    ) : StackSource
}
