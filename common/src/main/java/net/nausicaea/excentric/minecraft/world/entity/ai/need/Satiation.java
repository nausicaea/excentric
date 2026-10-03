package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.nausicaea.excentric.minecraft.world.entity.ai.*;

public record Satiation() implements Need {
	@Override
	public DecayFn decayFn() {
		return DecayFn.linear(1.0d);
	}

	@Override
	public UtilityFn<NeedsContext> utilityFn() {
		return UtilityFn.urgency();
	}
}
