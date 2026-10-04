package net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite;

/// Denotes a [Prerequisite] that is always satisfied.
public record Empty() implements Prerequisite {
	@Override
	public State poll() {
		return State.SATISFIED;
	}
}
