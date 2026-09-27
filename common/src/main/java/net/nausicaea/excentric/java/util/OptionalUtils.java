package net.nausicaea.excentric.java.util;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
final class OptionalUtils {
	private OptionalUtils() {
	}
	static <T> Iterable<T> iter(Optional<T> self) {
		return self.map(List::of).orElseGet(List::of);
	}
}
