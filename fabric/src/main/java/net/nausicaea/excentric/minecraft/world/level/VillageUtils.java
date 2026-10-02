package net.nausicaea.excentric.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.nausicaea.excentric.mixin.accessor.JigsawStructureAccessor;

import java.util.List;

final class VillageUtils {
	private VillageUtils() {
	}

	/// Find StructureStart instances for any villages that extend into the
	/// given chunk.
	static List<StructureStart> findStarts(StructureManager structureManager, ChunkPos chunkPos) {
		return structureManager.startsForStructure(chunkPos, structure -> {
			if (!(structure instanceof JigsawStructure jigsawStructure)) {
				return false;
			}
			// SAFETY: Final classes (JigsawStructure) must have the intermediate cast to
			// Object (see
			// https://docs.fabricmc.net/develop/mixins/accessors#accessors-for-final-classes).
			var startPool = ((JigsawStructureAccessor) (Object) jigsawStructure).villageMod$getStartPool();
			return startPool.unwrapKey().map(key -> key.location().getPath().endsWith("town_centers")).orElse(false);
		});
	}

	static BoundingBox extents(BlockPos anchor, int chunkRadius, int sectionHeight) {
		var halfWidth = 8 * chunkRadius;
		var halfHeight = 8 * sectionHeight;
		return new BoundingBox(anchor.getX() - halfWidth, anchor.getY() - halfHeight, anchor.getZ() - halfWidth,
		    anchor.getX() + halfWidth, anchor.getY() + halfHeight, anchor.getZ() + halfWidth);
	}
}
