package net.nausicaea.excentric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public final class ExcentricEventCallbacks {
	private ExcentricEventCallbacks() {
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
