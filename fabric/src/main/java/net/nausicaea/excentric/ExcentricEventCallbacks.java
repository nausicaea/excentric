package net.nausicaea.excentric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ExcentricEventCallbacks {
	private ExcentricEventCallbacks() {
	}

	public static void onServerChunkLoad(ServerLevel level, LevelChunk chunk) {
		CardinalComponents.LEVEL_CHUNK.maybeGet(chunk).ifPresent(c -> c.onLoad(level));
	}

	public static void onAfterBlockPlace(BlockState prevState, Level level, BlockPos pos, BlockState newState,
	    boolean movedByPiston) {
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
	}
}
