package net.nausicaea.excentric.ai.service

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

data class ItemServiceContext(
    val consumer: LivingEntity,
    val source: StackSource,
) {
    fun itemStack(): ItemStack =
        when (source) {
            is ForeignInventory -> source.container.getItem(source.slot)
            is Held -> consumer.getItemBySlot(source.slot)
            is Inventory -> source.container.getItem(source.slot)
            is World -> source.entity.item
        }

    /**
     * Careful: uses interior mutability!
     */
    fun putBack(stack: ItemStack) {
        when (this.source) {
            is ForeignInventory -> source.container.setItem(source.slot, stack)
            is Held -> consumer.setItemSlot(source.slot, stack)
            is Inventory -> source.container.setItem(source.slot, stack)
            is World -> source.entity.item = stack
        }
    }
}
