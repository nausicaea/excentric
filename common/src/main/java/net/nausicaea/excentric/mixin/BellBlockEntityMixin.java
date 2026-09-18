package net.nausicaea.excentric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.nausicaea.excentric.VillageRef;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(BellBlockEntity.class)
abstract class BellBlockEntityMixin extends BlockEntityMixin implements VillageRef {
	@Unique
	private static final String villageMod$TAG = "villageModVillageId";

	@Unique
	private UUID villageMod$villageId = null;

	@Override
	public Optional<UUID> villageMod$getVillageId() {
		return Optional.ofNullable(this.villageMod$villageId);
	}

	@Override
	public void villageMod$setVillageId(UUID id) {
		this.villageMod$villageId = id;
	}

	/// Store a [net.nausicaea.excentric.Village] [UUID] as custom
	/// persistent storage.
	@Override
	protected void villageMod$save(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
		if (villageMod$villageId != null) {
			tag.putUUID(villageMod$TAG, this.villageMod$villageId);
		}
	}

	/// Load a [net.nausicaea.excentric.Village] [UUID] from persistent storage.
	@Override
	protected void villageMod$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
		if (tag.hasUUID(villageMod$TAG)) {
			this.villageMod$villageId = tag.getUUID(villageMod$TAG);
		}
	}
}
