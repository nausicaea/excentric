package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

public record NeedsContext(NeedsCollection needs, Entity entity, ServerLevel level) implements UtilityFnContext {
	@Override
	public Entity entity() {
		return this.entity;
	}
}
