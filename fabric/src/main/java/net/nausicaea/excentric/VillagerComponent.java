package net.nausicaea.excentric;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;

class VillagerComponent extends VillageRefComponent {
	private final Villager entity;

	VillagerComponent(Villager villager) {
		this.entity = villager;
	}

	void onLoad(ServerLevel serverLevel) {
		if (villageId().isPresent()) {
			return;
		}

		CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData()).find(entity.blockPosition())
		    .ifPresent(v -> setVillageId(v.id()));
	}
}
