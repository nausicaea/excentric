package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.server.level.ServerLevel;

public record NeedsContext(NeedsCollection needs, ServerLevel level) {
}
