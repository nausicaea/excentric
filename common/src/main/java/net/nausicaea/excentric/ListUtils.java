package net.nausicaea.excentric;

import java.util.List;
import java.util.Optional;

final class ListUtils {
	private ListUtils() {
	}

	static <T> Optional<T> first(List<T> self) {
		return self.isEmpty() ? Optional.empty() : Optional.of(self.getFirst());
	}
}
