package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.resources.ResourceKey;

public interface Need {
	ResourceKey<Need> key();
	DecayFn decayFn();
	UtilityFn<NeedsContext> utilityFn();

	double initialSatisfaction();
}
