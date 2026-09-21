package net.nausicaea.excentric;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BellBlockEntity;

public final class BellComponent extends VillageRefComponent {
	private final BellBlockEntity blockEntity;

	public BellComponent(BellBlockEntity bbe) {
		this.blockEntity = bbe;
	}

	/// 1. Does the block have a village [java.util.UUID]?
	/// 2. If yes, early return. This case is expected to be seldom.
	/// 3. If not, query [VillageManager#findOrClaim] for the closest
	///    [net.nausicaea.excentric.Village] in range or trigger creation of one.
	/// 4. Record the [net.nausicaea.excentric.Village#id()] on the submitted block.
	public void onPlace(ServerLevel serverLevel, BlockPos blockPos) {
		if (villageId().isPresent()) {
			return;
		}

		var village = CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData()).findOrClaim(serverLevel,
		    GlobalPos.of(serverLevel.dimension(), blockPos));
		setVillageId(village.id());
	}
}
