package net.nausicaea.excentric.need;

import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

/// Collects extensions for Minecraft's [RandomSource].
public final class RandomSourceUtils {
	private RandomSourceUtils() {
	}

	/// Perform a weighted random selection from a list.
	public static <T> Optional<T> chooseWeighted(RandomSource rng, List<Weighted<T>> items) {
		double total = 0.0;
		for (Weighted<T> w : items) {
			double weight = w.weight();
			if (!(weight >= 0.0) || Double.isInfinite(weight)) {
				throw new IllegalArgumentException("Invalid weight: %s".formatted(weight));
			}
			total += weight;
		}
		if (total <= 0.0) {
			return Optional.empty();
		}
		double r = rng.nextDouble() * total;
		T lastPositive = null;
		for (Weighted<T> w : items) {
			if (w.weight() <= 0.0)
				continue;
			r -= w.weight();
			if (r < 0.0) {
				return Optional.of(w.item());
			}
			lastPositive = w.item();
		}
		return Optional.ofNullable(lastPositive);
	}
}
