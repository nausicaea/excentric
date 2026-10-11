package net.nausicaea.excentric.minecraft.server.level

import net.minecraft.server.level.ServerChunkCache
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.LevelChunk
import java.util.Optional

// TODO: candidate for common source set
fun ServerChunkCache.hasChunk(chunkPos: ChunkPos): Boolean = this.hasChunk(chunkPos.x, chunkPos.z)

fun ServerChunkCache.getChunkNow(chunkPos: ChunkPos): Optional<LevelChunk> = Optional.ofNullable(this.getChunkNow(chunkPos.x, chunkPos.z))
