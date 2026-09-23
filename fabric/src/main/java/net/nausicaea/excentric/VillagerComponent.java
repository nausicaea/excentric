package net.nausicaea.excentric;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;

public class VillagerComponent extends VillageRefComponent {
	private final Villager entity;

	public VillagerComponent(Villager villager) {
		this.entity = villager;
	}

	public void onLoad(ServerLevel serverLevel) {
		if (villageId().isPresent()) {
			return;
		}

		CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData()).find(entity.blockPosition())
		    .ifPresent(v -> setVillageId(v.id()));
	}
}
