package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public class ExcentricFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerAccess.register();
		ExcentricCommon.onInitialize();

		ServerChunkEvents.CHUNK_LOAD.register(
		    ((level, chunk) -> CardinalComponents.LEVEL_CHUNK.maybeGet(chunk).ifPresent(c -> c.onLoad(level))));
		// ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register((entity, world) -> {throw
		// new Todo();});
		// ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {throw new
		// Todo();});
		ExcentricEvents.AFTER_BLOCK_PLACE.register((prevState, level, pos, newState, movedByPiston) -> {
			if (!(level instanceof ServerLevel serverLevel)) {
				return;
			}
			if (!prevState.is(Blocks.BELL) || movedByPiston || prevState.is(newState.getBlock())) {
				return;
			}
			if (!(serverLevel.getBlockEntity(pos) instanceof BellBlockEntity bbe)) {
				return;
			}
			CardinalComponents.BELL.maybeGet(bbe).ifPresent(c -> c.onPlace(serverLevel, pos));
		});
	}
}
