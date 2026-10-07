package net.nausicaea.excentric.minecraft.world.entity.ai.service;

import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

public sealed interface StackSource {
	record Held(EquipmentSlot slot) implements StackSource {
	}

	record Inventory(Container container, int slot) implements StackSource {
	}

	record ForeignInventory(Container container, int slot, Vec3 pos) implements StackSource {
	}

	record World(ItemEntity entity) implements StackSource {
	}
}
