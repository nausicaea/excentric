package net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite;

/// Denotes something that needs to be satisfied as a pre-condition to something
/// else. The interface is modeled after a `Promise` or
/// [java.util.concurrent.Future].
public interface Prerequisite {
	/// The state of the [Prerequisite] after [Prerequisite#poll()] was last called.
	sealed interface State {
		record Pending() implements State {
		}
		record Satisfied() implements State {
		}
		record Failed() implements State {
		}
	}

	/// Poll the [Prerequisite], bringing it closer to satisfaction. Return the
	/// current [Prerequisite.State].
	State poll();
}
