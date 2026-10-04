package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.nausicaea.excentric.minecraft.world.entity.ai.*;

public record Satiation(DecayFn d, UtilityFn u) implements Need {
	public Satiation() {
		this(DecayFn.linear(1.0d), new UtilityFn.Example());
	}

	@Override
	public DecayFn decayFn() {
		return d;
	}

	@Override
	public UtilityFn utilityFn() {
		return u;
	}
}
