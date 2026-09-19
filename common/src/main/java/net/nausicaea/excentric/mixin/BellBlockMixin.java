package net.nausicaea.excentric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nausicaea.excentric.VillageManager;
import net.nausicaea.excentric.VillageRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BellBlock.class)
abstract class BellBlockMixin extends BlockBehaviourMixin {
	@Override
	protected void villageMod$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
	    boolean bl, CallbackInfo ci) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (!(level.getBlockEntity(blockPos) instanceof BellBlockEntity bbe)) {
			return;
		}
		VillageManager.get(serverLevel.getServer()).onBlockPlace(serverLevel, (VillageRef) bbe, blockPos);
	}
}
