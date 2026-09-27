package net.nausicaea.excentric.minecraft.world.entity.npc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.CardinalComponents;
import net.nausicaea.excentric.minecraft.world.level.VillageRefComponent;

public class VillagerComponent extends VillageRefComponent {
	private final Villager entity;

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "this component requires a reference to the connected entity, even if mutable.")
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
