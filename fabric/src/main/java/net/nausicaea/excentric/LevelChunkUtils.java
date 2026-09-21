package net.nausicaea.excentric;

import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.nausicaea.excentric.mixin.accessor.JigsawStructureAccessor;

import java.util.List;

public final class LevelChunkUtils {
	private LevelChunkUtils() {
	}

	/// Find StructureStart instances for any villages that extend into the
	/// given chunk.
	public static List<StructureStart> findVillageStarts(StructureManager structureManager, ChunkPos chunkPos) {
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

	public static BoundingBox chunkBoundingBox(Level level, ChunkPos chunkPos) {
		return new BoundingBox(chunkPos.getMinBlockX(), level.getMinY(), chunkPos.getMinBlockZ(),
		    chunkPos.getMaxBlockX() + 1, level.getMaxY(), chunkPos.getMaxBlockZ() + 1);
	}

	/// Calculate the global position of a structure piece.
	public static GlobalPos piecePos(ResourceKey<Level> dimension, StructurePiece piece) {
		return GlobalPos.of(dimension, piece.getLocatorPosition());
	}
}
