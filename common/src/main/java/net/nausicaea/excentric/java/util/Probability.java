package net.nausicaea.excentric.java.util;

import java.util.Collection;

public final class Probability {
	public static final Probability ZERO = new Probability(0.0);
	public static final Probability ONE = new Probability(1.0);

	private final double value;

	private Probability(double value) {
		this.value = value;
	}

	public double get() {
		return value;
	}

	public static Probability of(double value) {
		return new Probability(DoubleUtils.clamp01(value));
	}

	public Probability add(Probability rhs) {
		return Probability.of(this.value + rhs.value);
	}

	public Probability mul(Probability rhs) {
		return Probability.of(this.value * rhs.value);
	}

	public Probability inv() {
		return Probability.of(1.0 - this.value);
	}

	public boolean isSubnormal() {
		return this.value < Double.MIN_NORMAL;
	}

	public static Probability sum(Probability a, Probability b) {
		return Probability.of(a.value + b.value);
	}

	/// Calculate the arithmetic mean of a collection of [Probability]s. Returns
	/// [Probability#ZERO] if the collection is empty.
	public static Probability mean(Collection<Probability> src) {
		var len = src.size();
		if (len == 0) {
			return Probability.ZERO;
		}
		return Probability.of(src.stream().map(Probability::get).reduce(0.0d, Double::sum) / len);
	}

	/// Transform a subjective utility value to a probability-like value
	/// by `exp(utility/temperature)-1`.
	public static Probability boltzmann(Probability utility, Probability temperature) {
		return Probability.of(Math.expm1(utility.value / temperature.value));
	}
}
