package net.nausicaea.excentric.fabric.api.event

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.level.block.entity.BellBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.nausicaea.excentric.fabric.api.attachment.VillageRef.claim
import net.nausicaea.excentric.fabric.api.attachment.VillageRef.claimDeferred
import net.nausicaea.excentric.fabric.api.attachment.VillageRef.drainDeferred
import net.nausicaea.excentric.fabric.api.attachment.VillageRef.get
import net.nausicaea.excentric.village.Village
import net.nausicaea.excentric.village.Villages
import net.nausicaea.excentric.util.EveryN
import net.nausicaea.excentric.util.forEach
import net.nausicaea.excentric.util.orElse

object EventCallbacks {
    private val every10Ticks = EveryN(8)

    @JvmStatic
    fun register() {
        ServerTickEvents.END_SERVER_TICK.register(::onEndTick)
        ServerChunkEvents.CHUNK_LOAD.register(::onChunkLoad)
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(::onBlockEntityLoad)
        ServerEntityEvents.ENTITY_LOAD.register(::onEntityLoad)
        AFTER_BLOCK_PLACE.register(::onAfterBlockPlace)
    }

    private fun onEndTick(server: MinecraftServer) {
        drainDeferred()
        every10Ticks.run {
            server.allLevels
                .forEach { level -> Villages.get(level).debug(level) }
        }
    }

    private fun onChunkLoad(
        level: ServerLevel,
        chunk: ChunkAccess,
    ) {
        if (get(chunk).isSome()) {
            return
        }

        val vman = Villages.get(level)
        val chunkPos = chunk.pos
        vman
            .find(chunkPos.getMiddleBlockPosition(80))
            .orElse { vman.findOrClaimByStructure(level, chunkPos) }
            .toOptional() // Claim the chunk. Don't reconcile the chunk (i.e. claim anything that resides
            // on the chunk itself) here because it will trigger a complete chunk load.
            .ifPresent { village: Village? -> claim(chunk, village!!.id) }
    }

    private fun onAfterBlockPlace(
        @Suppress("unused") prevState: BlockState,
        pos: BlockPos,
        serverLevel: ServerLevel,
    ) {
        val be = serverLevel.getBlockEntity(pos)
        if (be !is BellBlockEntity) {
            return
        }
        if (get(be).isSome()) {
            return
        }
        val village = Villages.get(serverLevel).findOrClaim(serverLevel, pos)
        claim(be, village.id)
    }

    private fun onBlockEntityLoad(
        blockEntity: BlockEntity,
        serverLevel: ServerLevel,
    ) {
        if (blockEntity !is BellBlockEntity) {
            return
        }
        if (get(blockEntity).isSome()) {
            return
        }
        Villages
            .get(serverLevel)
            .find(blockEntity.blockPos)
            .forEach { v -> claimDeferred(blockEntity, v.id) }
    }

    private fun onEntityLoad(
        entity: Entity,
        serverLevel: ServerLevel,
    ) {
        if (entity !is Villager) {
            return
        }
        if (get(entity).isSome()) {
            return
        }

        Villages
            .get(serverLevel)
            .find(entity.blockPosition())
            .forEach { village -> claim(entity, village.id) }
    }
}
