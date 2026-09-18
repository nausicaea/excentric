package net.nausicaea.excentric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.nausicaea.excentric.Todo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BellBlock.class)
abstract class BellBlockMixin extends BlockBehaviourMixin {
	@Override
	protected void villageMod$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
	    boolean bl, CallbackInfo ci) {
		throw new Todo();
	}
}
