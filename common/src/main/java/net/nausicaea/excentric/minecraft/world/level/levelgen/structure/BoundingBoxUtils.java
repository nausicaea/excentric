package net.nausicaea.excentric.minecraft.world.level.levelgen.structure;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

	public final static StreamCodec<ByteBuf, BoundingBox> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.VAR_INT,
	    BoundingBox::minX, ByteBufCodecs.VAR_INT, BoundingBox::minY, ByteBufCodecs.VAR_INT, BoundingBox::minZ,
	    ByteBufCodecs.VAR_INT, BoundingBox::maxX, ByteBufCodecs.VAR_INT, BoundingBox::maxY, ByteBufCodecs.VAR_INT,
	    BoundingBox::maxZ, BoundingBox::new);

	private BoundingBoxUtils() {
	}
}
