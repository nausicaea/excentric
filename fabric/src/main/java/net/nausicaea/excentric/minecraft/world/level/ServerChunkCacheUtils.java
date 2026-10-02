package net.nausicaea.excentric.minecraft.world.level;

import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Optional;

public final class ServerChunkCacheUtils {
	public static boolean hasChunk(ServerChunkCache storage, ChunkPos chunkPos) {
		return storage.hasChunk(chunkPos.x, chunkPos.z);
	}
	public static Optional<LevelChunk> getChunkNow(ServerChunkCache storage, ChunkPos chunkPos) {
		return Optional.ofNullable(storage.getChunkNow(chunkPos.x, chunkPos.z));
	}
	private ServerChunkCacheUtils() {
	}
}
