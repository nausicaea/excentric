package net.nausicaea.excentric.minecraft.server.level

import net.minecraft.server.level.ServerChunkCache
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.LevelChunk

fun ServerChunkCache.hasChunk(chunkPos: ChunkPos): Boolean = this.hasChunk(chunkPos.x, chunkPos.z)

fun ServerChunkCache.getChunkNow(chunkPos: ChunkPos): LevelChunk? = this.getChunkNow(chunkPos.x, chunkPos.z)
