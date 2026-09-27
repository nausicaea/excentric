package net.nausicaea.excentric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.Optional;

interface VillageManager {
	default Village findOrClaim(ServerLevel level, BlockPos anchor) {
		return find(anchor).orElseGet(() -> claim(level, anchor));
	}
	default Village findOrClaim(ServerLevel level, BlockPos anchor, BoundingBox extents) {
		return find(anchor).orElseGet(() -> claim(level, anchor, extents));
	}

	default Optional<Village> findOrClaimByStructure(ServerLevel serverLevel, ChunkPos pos) {
		var dimension = serverLevel.dimension();
		var villageStructures = VillageManagerUtils.findVillageStarts(serverLevel.structureManager(), pos);
		if (villageStructures.isEmpty()) {
			return Optional.empty();
		}

		// Assume there is only one village structure in the loaded chunk.
		var villageStructureStart = villageStructures.getFirst();
		var boundingBox = villageStructureStart.getBoundingBox();
		var villageStructurePieces = villageStructureStart.getPieces();
		if (villageStructurePieces.isEmpty()) {
			return Optional.empty();
		}

		// The first element starts the village. Note that the start may not be in the
		// currently loaded chunk. We're just using that information to record the
		// [Village#anchor].
		var startPiece = villageStructurePieces.getFirst();
		var village = findOrClaim(serverLevel, startPiece.getLocatorPosition(), boundingBox);
		if (BoundingBoxUtils.containedChunks(village.boundingBox()).noneMatch(pos::equals)) {
			return Optional.empty();
		}
		return Optional.of(village);
	}

	Optional<Village> find(BlockPos anchor);

	Village claim(ServerLevel level, BlockPos anchor);
	Village claim(ServerLevel level, BlockPos anchor, BoundingBox extents);
}
