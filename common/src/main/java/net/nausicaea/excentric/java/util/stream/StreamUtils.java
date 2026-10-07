package net.nausicaea.excentric.java.util.stream;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class StreamUtils {
	private StreamUtils() {
	}

	public static <T> Stream<Enumerated<T>> enumerate(Stream<T> stream) {
		AtomicInteger index = new AtomicInteger();
		return stream.map(item -> new Enumerated<>(index.getAndIncrement(), item));
	}

	public record Enumerated<T>(int index, T value) {
	}
}
