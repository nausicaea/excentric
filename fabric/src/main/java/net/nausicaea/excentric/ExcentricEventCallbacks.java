package net.nausicaea.excentric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.concurrent.atomic.AtomicInteger;

public final class ExcentricEventCallbacks {
	private static final int DEBUG_TICK_INTERVAL = 10;
	private static final AtomicInteger tickCounter = new AtomicInteger(0);

	private ExcentricEventCallbacks() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(ExcentricEventCallbacks::onEndTick);
		ServerChunkEvents.CHUNK_LOAD.register(ExcentricEventCallbacks::onChunkLoad);
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(ExcentricEventCallbacks::onBlockEntityLoad);
		ServerEntityEvents.ENTITY_LOAD.register(ExcentricEventCallbacks::onEntityLoad);
		ExcentricEvents.AFTER_BLOCK_PLACE.register(ExcentricEventCallbacks::onAfterBlockPlace);
	}

	public static void onEndTick(MinecraftServer server) {
		var ctr = tickCounter.getAndIncrement();
		if (ctr % DEBUG_TICK_INTERVAL == 0) {
			server.getAllLevels()
			    .forEach(level -> CardinalComponents.VILLAGE_MANAGER.get(level.getLevelData()).debug(level));
			tickCounter.set(0);
		}
	}

	public static void onChunkLoad(ServerLevel level, LevelChunk chunk) {
		CardinalComponents.LEVEL_CHUNK.maybeGet(chunk).ifPresent(c -> c.onLoad(level));
	}

	public static void onAfterBlockPlace(BlockState prevState, BlockPos pos, ServerLevel serverLevel) {
		if (!(serverLevel.getBlockEntity(pos) instanceof BellBlockEntity bbe)) {
			return;
		}
		CardinalComponents.BELL.maybeGet(bbe).ifPresent(c -> c.onPlace(serverLevel, pos));
	}

	public static void onBlockEntityLoad(BlockEntity blockEntity, ServerLevel serverLevel) {
		if (!(blockEntity instanceof BellBlockEntity bbe)) {
			return;
		}
		CardinalComponents.BELL.maybeGet(bbe).ifPresent(b -> b.onLoad(serverLevel));
	}

	public static void onEntityLoad(Entity entity, ServerLevel serverLevel) {
		if (!(entity instanceof Villager villager)) {
			return;
		}
		CardinalComponents.VILLAGER.maybeGet(villager).ifPresent(c -> c.onLoad(serverLevel));
	}
}
