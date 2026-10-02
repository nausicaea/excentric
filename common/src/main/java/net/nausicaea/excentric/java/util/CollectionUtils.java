package net.nausicaea.excentric.java.util;

import java.util.Collection;
import java.util.Optional;

public final class CollectionUtils {
	private CollectionUtils() {
	}
	public static <T> Optional<T> first(Collection<T> self) {
		return self.isEmpty() ? Optional.empty() : Optional.of(self.iterator().next());
	}

	/// Calculate the arithmetic mean of a collection of [Double]s. Returns `0.0d`
	/// if the collection is empty.
	public static double mean(Collection<Double> src) {
		var len = src.size();
		if (len == 0) {
			return 0.0d;
		}
		return src.stream().reduce(0.0d, Double::sum) / len;
	}
}
