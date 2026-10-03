package net.nausicaea.excentric.need;

import net.minecraft.resources.ResourceKey;

public interface Need {
	ResourceKey<Need> key();
	DecayFn decayFn();
	UtilityFn<NeedsContext> utilityFn();
	double satisfaction();
}
