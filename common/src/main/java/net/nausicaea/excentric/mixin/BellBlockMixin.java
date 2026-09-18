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
	/// 1. Does the bell have a village [java.util.UUID]?
	/// 2. If yes, early return
	/// 3. If not, query [net.nausicaea.excentric.VillageManager#findOrCreate] for
	///    the closest [net.nausicaea.excentric.Village] in range or trigger creation
	///    of one.
	/// 4. Record the [net.nausicaea.excentric.Village#id()] on the bell.
	@Override
	protected void villageMod$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
	    boolean bl, CallbackInfo ci) {
		throw new Todo();
	}
}
