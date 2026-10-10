package net.nausicaea.excentric.minecraft.world.level.levelgen.structure

import io.netty.buffer.ByteBuf
import net.minecraft.core.SectionPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import java.util.stream.Stream

fun BoundingBox.containedChunks(): Stream<ChunkPos> {
    val i = SectionPos.blockToSectionCoord(this.minX() + 15)
    val j = SectionPos.blockToSectionCoord(this.minZ() + 15)
    val k = SectionPos.blockToSectionCoord(this.maxX() + 1) - 1
    val l = SectionPos.blockToSectionCoord(this.maxZ() + 1) - 1
    return ChunkPos.rangeClosed(ChunkPos(i, j), ChunkPos(k, l))
}

val BOUNDING_BOX_STREAM_CODEC: StreamCodec<ByteBuf, BoundingBox> =
    StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        BoundingBox::minX,
        ByteBufCodecs.VAR_INT,
        BoundingBox::minY,
        ByteBufCodecs.VAR_INT,
        BoundingBox::minZ,
        ByteBufCodecs.VAR_INT,
        BoundingBox::maxX,
        ByteBufCodecs.VAR_INT,
        BoundingBox::maxY,
        ByteBufCodecs.VAR_INT,
        BoundingBox::maxZ,
        ::BoundingBox,
    )
