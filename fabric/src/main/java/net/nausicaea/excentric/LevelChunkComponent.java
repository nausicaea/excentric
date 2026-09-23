package net.nausicaea.excentric;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class LevelChunkComponent extends VillageRefComponent {
	private final ChunkAccess chunk;

	public LevelChunkComponent(ChunkAccess chunk) {
		this.chunk = chunk;
	}

	/// 1. Find the structure start chunk with
	///    [VillageManagerUtils#findVillageStarts]
	/// 2. Query [net.nausicaea.excentric.VillageManagerComponent#findOrClaim] for
	///    the closest [net.nausicaea.excentric.Village] in range or trigger creation
	///    of one.
	/// 3. Claim the current chunk by setting the village ID.
	public void onLoad(ServerLevel serverLevel) {
		if (villageId().isPresent()) {
			return;
		}

		var vman = CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData());
		var loadedChunkPos = chunk.getPos();
		vman.find(loadedChunkPos.getMiddleBlockPosition(80))
		    .or(() -> vman.findOrClaimByStructure(serverLevel, loadedChunkPos))
		    // Claim the chunk. Don't reconcile the chunk (i.e. claim anything that resides
		    // on the chunk itself) here because it will trigger a complete chunk load.
		    .ifPresent(village -> setVillageId(village.id()));
	}
}
