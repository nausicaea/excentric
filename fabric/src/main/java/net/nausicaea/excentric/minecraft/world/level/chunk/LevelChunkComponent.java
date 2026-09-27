package net.nausicaea.excentric.minecraft.world.level.chunk;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.nausicaea.excentric.CardinalComponents;
import net.nausicaea.excentric.minecraft.world.level.VillageRefComponent;

public final class LevelChunkComponent extends VillageRefComponent {
	private final ChunkAccess chunk;

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "the chunk's position is not known at this point, so we need a reference to it")
	public LevelChunkComponent(ChunkAccess chunk) {
		this.chunk = chunk;
	}

	public void onLoad(ServerLevel serverLevel) {
		if (villageId().isPresent()) {
			return;
		}

		var vman = CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData());
		var chunkPos = chunk.getPos();
		vman.find(chunkPos.getMiddleBlockPosition(80)).or(() -> vman.findOrClaimByStructure(serverLevel, chunkPos))
		    // Claim the chunk. Don't reconcile the chunk (i.e. claim anything that resides
		    // on the chunk itself) here because it will trigger a complete chunk load.
		    .ifPresent(village -> setVillageId(village.id()));
	}
}
