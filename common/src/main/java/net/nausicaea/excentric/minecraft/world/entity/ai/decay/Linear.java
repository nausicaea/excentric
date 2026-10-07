package net.nausicaea.excentric.minecraft.world.entity.ai.decay;

import net.nausicaea.excentric.java.util.Probability;

/// A positive `baseRate` will result in a negative slope for the linear decay.
public record Linear(double baseRate) implements DecayFn {
	@Override
	public Probability decay(Probability value, double deltaTime) {
		return Probability.of(value.get() - baseRate * deltaTime);
	}
}
