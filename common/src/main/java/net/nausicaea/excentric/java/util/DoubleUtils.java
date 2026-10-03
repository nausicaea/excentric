package net.nausicaea.excentric.java.util;

public final class DoubleUtils {
	private DoubleUtils() {
	}

	/// This doesn't use [Math#clamp] on purpose to avoid one branch.
	public static double clamp01(double value) {
		// noinspection MathClampMigration
		return Math.min(Math.max(value, 0.0d), 1.0d);
	}

	/// Transform a subjective utility value to a probability-like value
	/// by `exp(utility/temperature)-1`.
	public static double boltzmann(double utility, double temperature) {
		return clamp01(Math.expm1(utility / temperature));
	}
}
