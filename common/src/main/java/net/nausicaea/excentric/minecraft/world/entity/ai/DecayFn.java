package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.nausicaea.excentric.java.util.DoubleUtils;

public sealed interface DecayFn {
	/// A positive `baseRate` will result in a negative slope for the linear decay.
	static DecayFn linear(double baseRate) {
		return new Linear(baseRate);
	}

	/// Reduce the `value` for the given time window.
	double decay(double value, double deltaTime);

	/// A positive `baseRate` will result in a negative slope for the linear decay.
	record Linear(double baseRate) implements DecayFn {
		public Linear() {
			this(1.0d);
		}

		@Override
		public double decay(double value, double deltaTime) {
			return DoubleUtils.clamp01(value - baseRate * deltaTime);
		}
	}
}
