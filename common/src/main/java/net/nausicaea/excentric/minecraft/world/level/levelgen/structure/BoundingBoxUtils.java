package net.nausicaea.excentric.minecraft.world.level.levelgen.structure;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.stream.Stream;

public final class BoundingBoxUtils {
	public static Stream<ChunkPos> containedChunks(BoundingBox b) {
		int i = SectionPos.blockToSectionCoord(b.minX() + 15);
		int j = SectionPos.blockToSectionCoord(b.minZ() + 15);
		int k = SectionPos.blockToSectionCoord(b.maxX() + 1) - 1;
		int l = SectionPos.blockToSectionCoord(b.maxZ() + 1) - 1;
		return ChunkPos.rangeClosed(new ChunkPos(i, j), new ChunkPos(k, l));
	}

	private BoundingBoxUtils() {
	}
}
