package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.nausicaea.excentric.minecraft.world.entity.ai.decay.DecayFn;
import net.nausicaea.excentric.minecraft.world.entity.ai.utility.CompoundBaUrAe;
import net.nausicaea.excentric.minecraft.world.entity.ai.utility.UtilityFn;

public record Satiation(DecayFn d, UtilityFn u) implements Need {
	public Satiation() {
		this(DecayFn.linear(1.0d), new CompoundBaUrAe());
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
