package net.nausicaea.excentric;

import net.minecraft.nbt.CompoundTag;

public final class VillagerData {
	public CompoundTag save() {
		return new CompoundTag();
	}

	public static VillagerData load(CompoundTag tag) {
		return new VillagerData();
	}
}
