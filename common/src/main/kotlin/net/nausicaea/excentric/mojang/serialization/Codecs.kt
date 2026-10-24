package net.nausicaea.excentric.mojang.serialization

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.mojang.serialization.codecs.UnboundedMapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.village.Village
import java.util.function.Function

/**
 * Serialisation codecs for persistent storage.
 */
object Codecs {
    val PROBABILITY: Codec<Probability> = Codec.DOUBLE.xmap(Probability::of, Probability::value)

    @JvmField
    val NEEDS_COLLECTION: UnboundedMapCodec<ResourceLocation, Probability> = Codec.unboundedMap(ResourceLocation.CODEC, PROBABILITY)
    val VILLAGE: Codec<Village> =
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
}
