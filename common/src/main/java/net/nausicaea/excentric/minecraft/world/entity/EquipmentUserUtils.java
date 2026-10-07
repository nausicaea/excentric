package net.nausicaea.excentric.minecraft.world.entity;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentUser;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public final class EquipmentUserUtils {
	private EquipmentUserUtils() {
	}
	public static Optional<ItemStack> getItemBySlot(EquipmentUser user, EquipmentSlot slot) {
		return Optional.of(user.getItemBySlot(slot)).filter(s -> !s.isEmpty());
	}
}
