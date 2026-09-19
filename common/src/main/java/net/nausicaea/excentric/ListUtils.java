package net.nausicaea.excentric;

import java.util.List;
import java.util.Optional;

public final class ListUtils {
	private ListUtils() {
	}

	public static <T> Optional<T> first(List<T> self) {
		return self.isEmpty() ? Optional.empty() : Optional.of(self.getFirst());
	}
}
