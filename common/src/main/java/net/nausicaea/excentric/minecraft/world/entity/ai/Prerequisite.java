package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.core.BlockPos;
import net.nausicaea.excentric.Todo;

/// Denotes something that needs to be satisfied as a pre-condition to something
/// else. The interface is modeled after a `Promise` or
/// [java.util.concurrent.Future].
public sealed interface Prerequisite {
	/// Static instance of the empty prerequisite (i.e. always satisfied).
	Prerequisite EMPTY = new Empty();

	/// The state of the [Prerequisite] after [Prerequisite#poll()] was last called.
	enum State {
		PENDING, SATISFIED
	}

	/// Poll the [Prerequisite], bringing it closer to satisfaction. Return the
	/// current [Prerequisite.State].
	State poll();

	/// Denotes a [Prerequisite] that is always satisfied.
	record Empty() implements Prerequisite {
		@Override
		public State poll() {
			return State.SATISFIED;
		}
	}

	/// Denotes a [Prerequisite] position: the entity that wants to satisfy this
	/// condition must navigate to the stored [BlockPos].
	record Position(BlockPos pos) implements Prerequisite {
		@Override
		public State poll() {
			throw new Todo();
		}
	}
}
