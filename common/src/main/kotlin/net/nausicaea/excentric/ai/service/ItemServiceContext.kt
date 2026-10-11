package net.nausicaea.excentric.ai.service

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

data class ItemServiceContext(
    val consumer: LivingEntity,
    val source: StackSource,
) {
    fun itemStack(): ItemStack =
        when (source) {
            is StackSource.ForeignInventory -> source.container.getItem(source.slot)
            is StackSource.Held -> consumer.getItemBySlot(source.slot)
            is StackSource.Inventory -> source.container.getItem(source.slot)
            is StackSource.World -> source.entity.item
        }

    /**
     * Careful: uses interior mutability!
     */
    fun putBack(stack: ItemStack) {
        when (this.source) {
            is StackSource.ForeignInventory -> source.container.setItem(source.slot, stack)
            is StackSource.Held -> consumer.setItemSlot(source.slot, stack)
            is StackSource.Inventory -> source.container.setItem(source.slot, stack)
            is StackSource.World -> source.entity.item = stack
        }
    }
}
