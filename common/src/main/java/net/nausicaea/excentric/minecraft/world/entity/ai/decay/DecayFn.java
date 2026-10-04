package net.nausicaea.excentric.minecraft.world.entity.ai.decay;

public interface DecayFn {
	/// A positive `baseRate` will result in a negative slope for the linear decay.
	static DecayFn linear(double baseRate) {
		return new Linear(baseRate);
	}

	/// Reduce the `value` for the given time window.
	double decay(double value, double deltaTime);
}
