package net.nausicaea.excentric;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;

public class VillagerComponent extends VillageRefComponent {
	private final Villager entity;

	public VillagerComponent(Villager villager) {
		this.entity = villager;
	}

	public void onLoad(ServerLevel serverLevel) {
		CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData())
		    .find(GlobalPos.of(serverLevel.dimension(), entity.blockPosition())).ifPresent(v -> setVillageId(v.id()));
	}
}
