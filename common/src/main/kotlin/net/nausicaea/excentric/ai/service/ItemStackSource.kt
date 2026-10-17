package net.nausicaea.excentric.ai.service

import net.minecraft.world.Container
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.phys.Vec3

/**
 * Represents where an [net.minecraft.world.item.ItemStack] is from.
 */
sealed interface ItemStackSource {
    /**
     * An [net.minecraft.world.item.ItemStack] may be held by an [net.minecraft.world.entity.Entity] in a particular [EquipmentSlot].
     */
    data class Held(
        val slot: EquipmentSlot,
    ) : ItemStackSource

    /**
     * An [net.minecraft.world.item.ItemStack] may reside in an [net.minecraft.world.entity.Entity]'s own inventory.
     */
    data class Inventory(
        val container: Container,
        val slot: Int,
    ) : ItemStackSource

    /**
     * An [net.minecraft.world.item.ItemStack] may reside some [Container] somewhere in the [net.minecraft.world.level.Level].
     */
    data class ForeignInventory(
        val container: Container,
        val slot: Int,
        val pos: Vec3,
    ) : ItemStackSource

    /**
     * An [net.minecraft.world.item.ItemStack] may have been dropped at some position in the [net.minecraft.world.level.Level]  as an [ItemEntity].
     */
    data class World(
        val entity: ItemEntity,
    ) : ItemStackSource
}
