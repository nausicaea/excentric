package net.nausicaea.excentric.village

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BOUNDING_BOX_STREAM_CODEC
import java.util.UUID
import java.util.function.Function

/**
 * Defines identity, location, and size of a village.
 */
data class Village(
    /**
     * The unique identifier of a village.
     */
    val id: UUID,
    /**
     * The anchoring position of the village, i.e. where it was first claimed from.
     */
    val anchor: BlockPos,
    /**
     * The "centre of mass" of the village. May be different from the anchor.
     */
    val center: BlockPos,
    /**
     * The integer volume around the [Village.anchor] that defines the extends of the village.
     */
    val boundingBox: BoundingBox,
) {
    companion object {
        /**
         * Serialisation codec for persistent storage.
         */
        val CODEC: Codec<Village> =
            RecordCodecBuilder.create(
                Function { i: RecordCodecBuilder.Instance<Village> ->
                    i
                        .group(
                            UUIDUtil.CODEC.fieldOf("id").forGetter(Village::id),
                            BlockPos.CODEC
                                .fieldOf("anchor")
                                .forGetter(Village::anchor),
                            BlockPos.CODEC
                                .fieldOf("center")
                                .forGetter(Village::center),
                            BoundingBox.CODEC
                                .fieldOf("boundingBox")
                                .forGetter(Village::boundingBox),
                        ).apply(i, ::Village)
                },
            )

        /**
         * Serialisation codec for network synchronisation.
         */
        val STREAM_CODEC: StreamCodec<ByteBuf, Village> =
            StreamCodec.composite(
                UUIDUtil.STREAM_CODEC,
                Village::id,
                BlockPos.STREAM_CODEC,
                Village::anchor,
                BlockPos.STREAM_CODEC,
                Village::center,
                BOUNDING_BOX_STREAM_CODEC,
                Village::boundingBox,
                ::Village,
            )
    }
}
