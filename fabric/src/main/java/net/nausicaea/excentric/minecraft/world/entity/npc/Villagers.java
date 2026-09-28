package net.nausicaea.excentric.minecraft.world.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.minecraft.world.level.VillageRefs;

public final class Villagers {
	public static void onLoad(ServerLevel serverLevel, Villager villager) {
		if (VillageRefs.get(villager).isPresent()) {
			return;
		}

		Villages.get(serverLevel).find(villager.blockPosition())
		    .ifPresent(village -> VillageRefs.claim(villager, village.id()));
	}
	private Villagers() {
	}
}
