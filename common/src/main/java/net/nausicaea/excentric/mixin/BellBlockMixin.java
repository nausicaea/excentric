package net.nausicaea.excentric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nausicaea.excentric.ExcentricCommon;
import net.nausicaea.excentric.VillageManager;
import net.nausicaea.excentric.VillageRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BellBlock.class)
abstract class BellBlockMixin extends BlockBehaviourMixin {
	@Unique
	private final static Logger villageMod$LOG = LoggerFactory.getLogger(BellBlockMixin.class);

	/// 1. Does the bell have a village [java.util.UUID]?
	/// 2. If yes, early return
	/// 3. If not, query [net.nausicaea.excentric.VillageManager#findOrCreate] for
	///    the closest [net.nausicaea.excentric.Village] in range or trigger creation
	///    of one.
	/// 4. Record the [net.nausicaea.excentric.Village#id()] on the bell.
	@Override
	protected void villageMod$onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2,
	    boolean bl, CallbackInfo ci) {
		if (level.getBlockEntity(blockPos) instanceof BellBlockEntity bbe) {
			// TODO: figure out if the village ID is persistent if the block is broken and
			// re-placed somewhere else.
			var villageIdPresent = ((VillageRef) bbe).villageMod$getVillageId().map(id -> {
				villageMod$LOG.warn(ExcentricCommon.LOG_MARKER,
				    "Newly placed bell block at {} already has a reference to village {}", blockPos, id);
				return true;
			}).orElse(false);
			if (villageIdPresent) {
				return;
			}

			var server = level.getServer();
			if (server == null) {
				villageMod$LOG.error(ExcentricCommon.LOG_MARKER, "Server reference is null from level {}", level);
				return;
			}
			var village = VillageManager.get(server).findOrCreate(GlobalPos.of(level.dimension(), blockPos));
			((VillageRef) bbe).villageMod$setVillageId(village.id());
		}
	}
}
