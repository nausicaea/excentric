package net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite;

import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.Todo;

/// Denotes a [Prerequisite] position: the entity that wants to satisfy this
/// condition must navigate to the stored [Vec3].
public record Position(Vec3 pos) implements Prerequisite {
	@Override
	public State poll() {
		throw new Todo();
	}
}
