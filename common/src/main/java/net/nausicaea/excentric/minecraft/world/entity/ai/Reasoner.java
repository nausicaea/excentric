package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.util.RandomSource;

import java.util.Optional;
import java.util.stream.Stream;

/// Describes a utility-based AI reasoning system.
public interface Reasoner<A, C> {
	/// Set the currently active action.
	void setAction(A action);
	/// Collect actions from a context.
	Stream<A> collect(C context);
	/// Score actions relative to a context.
	Stream<Weighted<A>> score(Stream<A> actions, C context);
	/// Based on the action weights, select at most one next action.
	Optional<A> select(Stream<Weighted<A>> scoredActions, RandomSource rng);
	/// Plan the next action for the [Reasoner]:
	/// 1. Collect potential actions
	/// 2. Score each action
	/// 3. Select the next action
	default void plan(C context, RandomSource rng) {
		select(score(collect(context), context), rng).ifPresent(this::setAction);
	}
}
