package net.nausicaea.excentric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.nausicaea.excentric.ExcentricEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
abstract class BlockBehaviourMixin {
	/// Hook into [net.minecraft.world.level.block.Block] placement.
	@Inject(method = "onPlace(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Z)V", at = @At("TAIL"))
	protected void villageMod$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
	    boolean bl, CallbackInfo ci) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return;
		}
		if (bl || blockState.is(blockState2.getBlock())) {
			return;
		}
		ExcentricEvents.AFTER_BLOCK_PLACE.invoker().afterBlockPlace(blockState, blockPos, serverLevel);
	}
}
