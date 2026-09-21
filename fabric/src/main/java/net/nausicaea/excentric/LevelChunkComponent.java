package net.nausicaea.excentric;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

public final class LevelChunkComponent extends VillageRefComponent {
	private final ChunkAccess chunk;

	public LevelChunkComponent(ChunkAccess chunk) {
		this.chunk = chunk;
	}

	/// 1. Find the structure start chunk with [LevelChunkUtils#findVillageStarts]
	/// 2. Query [net.nausicaea.excentric.VillageManagerComponent#findOrClaim] for
	///    the closest [net.nausicaea.excentric.Village] in range or trigger creation
	///    of one.
	/// 3. Claim the current chunk by setting the village ID.
	public void onLoad(ServerLevel serverLevel) {
		if (villageId().isPresent()) {
			return;
		}

		var dimension = serverLevel.dimension();
		var loadedChunkPos = chunk.getPos();
		var villageStructures = LevelChunkUtils.findVillageStarts(serverLevel.structureManager(), loadedChunkPos);
		if (villageStructures.isEmpty()) {
			return;
		}

		// Assume there is only one village structure in the loaded chunk.
		var villageStructureStart = villageStructures.getFirst();
		var boundingBox = villageStructureStart.getBoundingBox();
		var villageStructurePieces = villageStructureStart.getPieces();
		if (villageStructurePieces.isEmpty()) {
			return;
		}

		// The first element starts the village. Note that the start may not be in the
		// currently loaded chunk. We're just using that information to record the
		// [Village#anchor].
		var startPiece = villageStructurePieces.getFirst();
		// FIXME: this raises NoSuchElementException
		var village = CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData()).findOrClaim(serverLevel,
		    LevelChunkUtils.piecePos(dimension, startPiece), boundingBox);
		if (village.boundingBox().intersectingChunks().noneMatch(loadedChunkPos::equals)) {
			return;
		}

		// Claim the chunk. Don't reconcile the chunk (i.e. claim anything that resides
		// on the chunk itself) here because it will trigger a complete chunk load.
		setVillageId(village.id());
	}
}
