package net.nausicaea.excentric.ai.service

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

data class ItemServiceContext(
    val subject: LivingEntity,
    val source: ItemStackSource,
) {
    fun itemStack(): ItemStack =
        when (source) {
            is ItemStackSource.ForeignInventory -> source.container.getItem(source.slot)
            is ItemStackSource.Held -> subject.getItemBySlot(source.slot)
            is ItemStackSource.Inventory -> source.container.getItem(source.slot)
            is ItemStackSource.World -> source.entity.item
        }

    /**
     * Careful: uses interior mutability!
     */
    fun putBack(stack: ItemStack) {
        when (this.source) {
            is ItemStackSource.ForeignInventory -> source.container.setItem(source.slot, stack)
            is ItemStackSource.Held -> subject.setItemSlot(source.slot, stack)
            is ItemStackSource.Inventory -> source.container.setItem(source.slot, stack)
            is ItemStackSource.World -> source.entity.item = stack
        }
    }
}
