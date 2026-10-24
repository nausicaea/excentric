package net.nausicaea.excentric.village

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BOUNDING_BOX_STREAM_CODEC
import java.util.UUID

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
