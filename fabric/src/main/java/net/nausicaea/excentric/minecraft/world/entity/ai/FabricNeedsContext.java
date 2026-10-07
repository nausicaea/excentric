package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.world.entity.Entity;

public record FabricNeedsContext(NeedsCollection needs, Entity entity) implements NeedsContext {
}
