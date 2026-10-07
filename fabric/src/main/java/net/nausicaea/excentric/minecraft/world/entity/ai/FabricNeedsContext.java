package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.world.entity.Entity;
import net.nausicaea.excentric.Todo;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

import java.util.stream.Stream;

public record FabricNeedsContext(NeedsCollection needs, Entity entity) implements NeedsContext {
	public Stream<Service> collect() {
		throw new Todo();
	}
}
