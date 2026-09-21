package net.nausicaea.excentric;

import java.util.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class OptionalUtils {
	private OptionalUtils() {
	}
	public static <T> Iterable<T> iter(Optional<T> self) {
		return self.map(List::of).orElseGet(List::of);
	}
}
