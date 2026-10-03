package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.minecraft.world.entity.ai.*;

public record Satiation() implements Need {
	@Override
	public ResourceKey<Need> key() {
		return Needs.SATIATION.key();
	}

	@Override
	public DecayFn decayFn() {
		return DecayFn.linear(1.0d);
	}

	@Override
	public UtilityFn<NeedsContext> utilityFn() {
		return UtilityFn.urgency();
	}

	@Override
	public double initialSatisfaction() {
		return 1.0d;
	}
}
