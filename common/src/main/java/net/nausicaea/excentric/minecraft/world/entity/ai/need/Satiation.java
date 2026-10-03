package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.minecraft.world.entity.ai.*;

public record Satiation(DecayFn decayFn, UtilityFn<NeedsContext> utilityFn,
    double initialSatisfaction) implements Need {
	public Satiation() {
		this(DecayFn.linear(1.0d), UtilityFn.urgency(), 1.0d);
	}

	@Override
	public ResourceKey<Need> key() {
		return Needs.SATIATION.key();
	}

	@Override
	public DecayFn decayFn() {
		return decayFn;
	}

	@Override
	public UtilityFn<NeedsContext> utilityFn() {
		return utilityFn;
	}

	@Override
	public double initialSatisfaction() {
		return initialSatisfaction;
	}
}
