package net.nausicaea.excentric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nausicaea.excentric.VillageManager;
import net.nausicaea.excentric.VillageRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BellBlock.class)
abstract class BellBlockMixin extends BlockBehaviourMixin {
	@Override
	protected void villageMod$onPlace(BlockState prevState, Level level, BlockPos blockPos, BlockState newState,
	    boolean movedByPiston, CallbackInfo ci) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!prevState.is(Blocks.BELL) || movedByPiston || prevState.is(newState.getBlock())) {
			return;
		}
		if (!(serverLevel.getBlockEntity(blockPos) instanceof BellBlockEntity bbe)) {
			return;
		}
		VillageManager.get(serverLevel.getServer()).onBlockPlace(serverLevel, (VillageRef) bbe, blockPos);
	}
}
