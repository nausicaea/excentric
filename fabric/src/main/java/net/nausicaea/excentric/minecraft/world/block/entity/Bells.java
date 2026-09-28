package net.nausicaea.excentric.minecraft.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.minecraft.world.level.VillageRefs;

public final class Bells {
	public static void onPlace(ServerLevel level, BlockPos pos, BellBlockEntity bbe) {
		if (VillageRefs.get(bbe).isPresent()) {
			return;
		}
		var village = Villages.get(level).findOrClaim(level, pos);
		VillageRefs.claim(bbe, village.id());
	}

	public static void onLoad(ServerLevel level, BellBlockEntity bbe) {
		if (VillageRefs.get(bbe).isPresent()) {
			return;
		}
		Villages.get(level).find(bbe.getBlockPos()).ifPresent(v -> VillageRefs.claim(bbe, v.id()));
	}

	private Bells() {
	}
}
