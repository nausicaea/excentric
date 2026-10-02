package net.nausicaea.excentric.minecraft.world.level.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.minecraft.world.level.VillageRefs;

public abstract class LevelChunks {
	public static void onLoad(ServerLevel level, ChunkAccess chunk) {
		if (VillageRefs.get(chunk).isPresent()) {
			return;
		}

		var vman = Villages.get(level);
		var chunkPos = chunk.getPos();
		vman.find(chunkPos.getMiddleBlockPosition(80)).or(() -> vman.findOrClaimByStructure(level, chunkPos))
		    // Claim the chunk. Don't reconcile the chunk (i.e. claim anything that resides
		    // on the chunk itself) here because it will trigger a complete chunk load.
		    .ifPresent(village -> VillageRefs.claim(chunk, village.id()));
	}

	private LevelChunks() {
	}
}
