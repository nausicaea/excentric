package net.nausicaea.excentric;

import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Optional;

public interface VillageManager {
	default Village findOrClaim(ServerLevel level, GlobalPos anchor) {
		return find(anchor).orElseGet(() -> claim(level, anchor));
	}
	default Village findOrClaim(ServerLevel level, GlobalPos anchor, BoundingBox extents) {
		return find(anchor).orElseGet(() -> claim(level, anchor, extents));
	}

	Optional<Village> find(GlobalPos anchor);

	Village claim(ServerLevel level, GlobalPos anchor);
	Village claim(ServerLevel level, GlobalPos anchor, BoundingBox extents);
}
