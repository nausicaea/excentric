package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.nausicaea.excentric.java.util.DoubleUtils;

public sealed interface DecayFn {
	DecayFn LINEAR = new Linear();

	double decay(double value, double deltaTime);

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
