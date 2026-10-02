package net.nausicaea.excentric.need;

public sealed interface DecayFn {
	DecayFn LINEAR = new Linear();

	double decay(double value, double deltaTime);

	record Linear(double baseRate) implements DecayFn {
		public Linear() {
			this(1.0d);
		}

		@Override
		public double decay(double value, double deltaTime) {
			return value * baseRate * deltaTime;
		}
	}
}
