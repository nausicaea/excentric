package net.nausicaea.excentric.minecraft.world.level

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.StructureStart
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.containedChunks
import net.nausicaea.excentric.util.None
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Some
import net.nausicaea.excentric.util.unwrapOrElse

// TODO: candidate for common source set
interface VillageManager {
    fun findOrClaim(
        level: ServerLevel,
        anchor: BlockPos,
    ): Village = find(anchor).unwrapOrElse { claim(level, anchor) }

    fun findOrClaim(
        level: ServerLevel,
        anchor: BlockPos,
        extents: BoundingBox,
    ): Village =
        find(anchor).unwrapOrElse {
            claim(level, anchor, extents)
        }

    fun findOrClaimByStructure(
        serverLevel: ServerLevel,
        pos: ChunkPos,
    ): Option<Village> {
        val villageStructures = VillageUtils.findStarts(serverLevel.structureManager(), pos)
        if (villageStructures.isEmpty()) {
            return None
        }

        // Assume there is only one village structure in the loaded chunk.
        val villageStructureStart: StructureStart = villageStructures.first()
        val boundingBox = villageStructureStart.getBoundingBox()
        val villageStructurePieces = villageStructureStart.pieces
        if (villageStructurePieces.isEmpty()) {
            return None
        }

        // The first element starts the village. Note that the start may not be in the
        // currently loaded chunk. We're just using that information to record the
        // [Village#anchor].
        val startPiece: StructurePiece = villageStructurePieces.first()
        val village = findOrClaim(serverLevel, startPiece.locatorPosition, boundingBox)
        if (village.boundingBox.containedChunks().noneMatch { p -> pos == p }) {
            return None
        }
        return Some(village)
    }

    fun find(anchor: BlockPos): Option<Village>

    fun claim(
        level: ServerLevel,
        anchor: BlockPos,
    ): Village

    fun claim(
        level: ServerLevel,
        anchor: BlockPos,
        extents: BoundingBox,
    ): Village
}
