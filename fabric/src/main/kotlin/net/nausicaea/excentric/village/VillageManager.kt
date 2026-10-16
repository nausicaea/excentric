package net.nausicaea.excentric.village

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.levelgen.structure.StructurePiece
import net.minecraft.world.level.levelgen.structure.StructureStart
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.containedChunks
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.structures.startPool
import net.nausicaea.excentric.util.Opt
import net.nausicaea.excentric.util.toOpt
import net.nausicaea.excentric.util.unwrapOr
import net.nausicaea.excentric.util.unwrapOrElse

interface VillageManager {
    fun findOrClaim(
        level: ServerLevel,
        anchor: BlockPos,
    ): Village = find(anchor) ?: claim(level, anchor)

    fun findOrClaim(
        level: ServerLevel,
        anchor: BlockPos,
        extents: BoundingBox,
    ): Village = find(anchor) ?: claim(level, anchor, extents)

    fun findOrClaimByStructure(
        serverLevel: ServerLevel,
        pos: ChunkPos,
    ): Village? {
        val villageStructures = findStarts(serverLevel.structureManager(), pos)
        if (villageStructures.isEmpty()) {
            return null
        }

        // Assume there is only one village structure in the loaded chunk.
        val villageStructureStart: StructureStart = villageStructures.first()
        val boundingBox = villageStructureStart.getBoundingBox()
        val villageStructurePieces = villageStructureStart.pieces
        if (villageStructurePieces.isEmpty()) {
            return null
        }

        // The first element starts the village. Note that the start may not be in the
        // currently loaded chunk. We're just using that information to record the
        // [Village#anchor].
        val startPiece: StructurePiece = villageStructurePieces.first()
        val village = findOrClaim(serverLevel, startPiece.locatorPosition, boundingBox)
        if (!village.boundingBox.containedChunks().any { p -> pos == p }) {
            return null
        }
        return village
    }

    fun find(anchor: BlockPos): Village?

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

/**
 * Find StructureStart instances for any villages that extend into the
 * given chunk.
 */
private fun findStarts(
    structureManager: StructureManager,
    chunkPos: ChunkPos,
): MutableList<StructureStart> =
    structureManager.startsForStructure(
        chunkPos,
    ) { structure ->
        structure is JigsawStructure &&
            structure.startPool
                .unwrapKey()
                .toOpt()
                .map {
                    it.location().path.endsWith("town_centers")
                }.unwrapOr(false)
    }
