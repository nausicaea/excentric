package net.nausicaea.excentric;

import net.minecraft.world.entity.npc.Villager;

public class VillagerComponent extends VillageRefComponent {
	private final Villager entity;

	public VillagerComponent(Villager villager) {
		this.entity = villager;
	}
}
