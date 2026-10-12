package net.nausicaea.excentric.village

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.DustParticleOptions
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.entity.ai.village.poi.PoiManager
import net.minecraft.world.entity.ai.village.poi.PoiRecord
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.MOD_ID
import net.nausicaea.excentric.fabric.api.attachment.VillageRef
import net.nausicaea.excentric.minecraft.server.level.getChunkNow
import net.nausicaea.excentric.minecraft.server.level.hasChunk
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.containedChunks
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.showEdges
import net.nausicaea.excentric.minecraft.world.phys.mapMean
import net.nausicaea.excentric.minecraft.world.phys.toBlockPosFloor
import net.nausicaea.excentric.util.unwrapOr
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class Villages :
    SavedData(),
    VillageManager {
    private val villages: MutableMap<UUID, Village> = HashMap()

    override fun save(
        compoundTag: CompoundTag,
        provider: HolderLookup.Provider,
    ): CompoundTag {
        val serialized = Data(this.villages.values.toList())
        return CODEC.encodeStart<Tag?>(NbtOps.INSTANCE, serialized).getPartialOrThrow() as CompoundTag
    }

    override fun claim(
        level: ServerLevel,
        anchor: BlockPos,
    ): Village {
        val extents = extents(anchor, CHUNK_RADIUS, SECTION_HEIGHT)
        return claim(level, anchor, extents)
    }

    /**
     * Claim a new village
     *
     * 1. Determine [Village.anchor]
     * 2. Determine all beds
     *    ([PoiTypes.HOME]) within
     *    [CHUNK_RADIUS]
     * 3. Calculate [Village.center] from the beds
     * 4. Claim only the chunks within the bounding box of the village.
     */
    override fun claim(
        level: ServerLevel,
        anchor: BlockPos,
        extents: BoundingBox,
    ): Village {
        val poiManager = level.poiManager
        LOG.info(
            LOG_MARKER,
            "New village with volume centered at {}: {}x{}x{}",
            anchor,
            extents.xSpan,
            extents.ySpan,
            extents.zSpan,
        )
        val homes: List<PoiRecord> =
            poiManager
                .getInSquare(
                    { p -> p.`is`(PoiTypes.HOME) },
                    anchor,
                    16 * CHUNK_RADIUS,
                    PoiManager.Occupancy.ANY,
                ).filter { p -> extents.isInside(p.pos) }
                .toList()
        LOG.info(LOG_MARKER, "Found {} homes / beds", homes.size)
        val centroid =
            homes
                .mapMean { p -> Vec3(p.pos) }
                .unwrapOr(Vec3(anchor))
                .toBlockPosFloor()
        val village = Village(UUID.randomUUID(), anchor, centroid, extents)
        villages[village.id] = village
        claimLoadedChunks(level, village)
        return village
    }

    // / This silently discards multiple matching villages
    override fun find(anchor: BlockPos): Village? =
        villages.values
            .stream()
            .filter { v -> v.boundingBox.isInside(anchor) }
            .findFirst()
            .getOrNull()

    fun debug(level: ServerLevel) {
        val particleColor = 0x0088ff
        villages.values
            .forEach { v ->
                v.boundingBox
                    .showEdges(
                        level,
                        DustParticleOptions(particleColor, 1f),
                        1.0,
                    )
            }
    }

    private data class Data(
        val villages: List<Village>,
    )

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Villages::class.java)
        private const val ID: String = MOD_ID + "_villages"
        private val FACTORY =
            Factory(
                ::Villages,
                Villages::load,
                DataFixTypes.LEVEL,
            )
        private const val CHUNK_RADIUS = 4
        private const val SECTION_HEIGHT = 3

        fun get(level: ServerLevel): Villages = level.dataStorage.computeIfAbsent(FACTORY, ID)

        private fun load(
            tag: CompoundTag,
            @Suppress("unused") registries: HolderLookup.Provider,
        ): Villages {
            val state = Villages()
            CODEC
                .parse(NbtOps.INSTANCE, tag)
                .resultOrPartial { err ->
                    LOG.error(
                        LOG_MARKER,
                        "Failed to parse village data: {}",
                        err,
                    )
                }.ifPresent { data ->
                    data.villages.forEach { v ->
                        state.villages[v.id] = v
                    }
                }
            return state
        }

        private fun claimLoadedChunks(
            level: ServerLevel,
            village: Village,
        ) {
            val chunkSource = level.chunkSource
            val villageId = village.id
            village.boundingBox
                .containedChunks()
                .filter { chunkPos -> chunkSource.hasChunk(chunkPos) }
                .flatMap { chunkPos -> chunkSource.getChunkNow(chunkPos).stream() }
                .forEach { chunk -> VillageRef.claim(chunk, villageId) }
        }

        private val CODEC: Codec<Data> =
            RecordCodecBuilder.create { i ->
                i
                    .group(
                        Codec
                            .list(Village.CODEC)
                            .fieldOf("villages")
                            .forGetter(Data::villages),
                    ).apply(i, ::Data)
            }
    }
}

@Suppress("SameParameterValue")
private fun extents(
    anchor: BlockPos,
    chunkRadius: Int,
    sectionHeight: Int,
): BoundingBox {
    val halfWidth = 8 * chunkRadius
    val halfHeight = 8 * sectionHeight
    return BoundingBox(
        anchor.x - halfWidth,
        anchor.y - halfHeight,
        anchor.z - halfWidth,
        anchor.x + halfWidth,
        anchor.y + halfHeight,
        anchor.z + halfWidth,
    )
}
