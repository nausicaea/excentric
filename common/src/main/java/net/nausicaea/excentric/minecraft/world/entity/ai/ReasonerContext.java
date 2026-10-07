package net.nausicaea.excentric.minecraft.world.entity.ai;

import java.util.stream.Stream;

public interface ReasonerContext<A> {
	/// Collect services from various sources.
	Stream<A> collect();
}
