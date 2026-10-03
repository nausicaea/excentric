package net.nausicaea.excentric.fabric.api.event;

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
import net.nausicaea.excentric.EveryN;
import net.nausicaea.excentric.minecraft.world.block.entity.Bells;
import net.nausicaea.excentric.minecraft.world.entity.npc.Villagers;
import net.nausicaea.excentric.minecraft.world.level.VillageRefs;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.minecraft.world.level.chunk.LevelChunks;

public final class EventCallbacks {
	private static final EveryN every10Ticks = new EveryN(8);

	private EventCallbacks() {
	}

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(EventCallbacks::onEndTick);
		ServerChunkEvents.CHUNK_LOAD.register(LevelChunks::onLoad);
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(EventCallbacks::onBlockEntityLoad);
		ServerEntityEvents.ENTITY_LOAD.register(EventCallbacks::onEntityLoad);
		ExcentricEvents.AFTER_BLOCK_PLACE.register(EventCallbacks::onAfterBlockPlace);
	}

	private static void onEndTick(MinecraftServer server) {
		VillageRefs.drainDeferred();
		every10Ticks.run(() -> server.getAllLevels().forEach(level -> Villages.get(level).debug(level)));
	}

	private static void onAfterBlockPlace(BlockState prevState, BlockPos pos, ServerLevel serverLevel) {
		if (!(serverLevel.getBlockEntity(pos) instanceof BellBlockEntity bbe)) {
			return;
		}
		Bells.onPlace(serverLevel, pos, bbe);
	}

	private static void onBlockEntityLoad(BlockEntity blockEntity, ServerLevel serverLevel) {
		if (!(blockEntity instanceof BellBlockEntity bbe)) {
			return;
		}
		Bells.onLoad(serverLevel, bbe);
	}

	private static void onEntityLoad(Entity entity, ServerLevel serverLevel) {
		if (!(entity instanceof Villager villager)) {
			return;
		}
		Villagers.onLoad(serverLevel, villager);
	}
}
