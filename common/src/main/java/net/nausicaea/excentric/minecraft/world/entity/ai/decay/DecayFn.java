package net.nausicaea.excentric.minecraft.world.entity.ai.decay;

import net.nausicaea.excentric.java.util.Probability;

public interface DecayFn {
	/// A positive `baseRate` will result in a negative slope for the linear decay.
	static DecayFn linear(double baseRate) {
		return new Linear(baseRate);
	}

	/// Reduce the `value` for the given time window.
	Probability decay(Probability value, double deltaTime);
}
