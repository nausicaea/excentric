package net.nausicaea.excentric.minecraft.world.entity.ai.service;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record ItemServiceContext(LivingEntity consumer, StackSource source) {
	public ItemStack itemStack() {
		return switch (this.source()) {
			case StackSource.Held held -> consumer.getItemBySlot(held.slot());
			case StackSource.ForeignInventory foreignInventory ->
			    foreignInventory.container().getItem(foreignInventory.slot());
			case StackSource.Inventory inventory -> inventory.container().getItem(inventory.slot());
			case StackSource.World world -> world.entity().getItem();
		};
	}

	public void putBack(ItemStack stack) {
		switch (this.source()) {
			case StackSource.ForeignInventory foreignInventory ->
			    foreignInventory.container().setItem(foreignInventory.slot(), stack);
			case StackSource.Held held -> consumer.setItemSlot(held.slot(), stack);
			case StackSource.Inventory inventory -> inventory.container().setItem(inventory.slot(), stack);
			case StackSource.World world -> world.entity().setItem(stack);
		}
	}
}
