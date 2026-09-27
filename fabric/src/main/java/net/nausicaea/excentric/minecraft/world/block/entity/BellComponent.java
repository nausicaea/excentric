package net.nausicaea.excentric.minecraft.world.block.entity;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.nausicaea.excentric.CardinalComponents;
import net.nausicaea.excentric.minecraft.world.level.VillageRefComponent;

public final class BellComponent extends VillageRefComponent {
	private final BellBlockEntity blockEntity;

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "this component requires a reference to the connected entity, even if mutable.")
	public BellComponent(BellBlockEntity bbe) {
		this.blockEntity = bbe;
	}

	public void onPlace(ServerLevel serverLevel, BlockPos blockPos) {
		if (villageId().isPresent()) {
			return;
		}

		var village = CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData()).findOrClaim(serverLevel,
		    blockPos);
		setVillageId(village.id());
	}

	public void onLoad(ServerLevel serverLevel) {
		if (villageId().isPresent()) {
			return;
		}

		CardinalComponents.VILLAGE_MANAGER.get(serverLevel.getLevelData()).find(blockEntity.getBlockPos())
		    .ifPresent(v -> setVillageId(v.id()));
	}
}
