package net.nausicaea.excentric.need;

import net.minecraft.server.level.ServerLevel;

public record NeedsContext(NeedsCollection needs, ServerLevel level) {
}
