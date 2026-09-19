package net.nausicaea.excentric.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.ExcentricCommon;
import net.nausicaea.excentric.VillageRef;
import net.nausicaea.excentric.VillagerData;
import net.nausicaea.excentric.VillagerDataHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(Villager.class)
abstract class VillagerMixin implements VillagerDataHolder, VillageRef {
	@Unique
	private static final Logger villageMod$LOG = LoggerFactory.getLogger(VillagerMixin.class);

	@Unique
	private static final String villageMod$ID_TAG = "villageModVillageId";
	@Unique
	private static final String villageMod$DATA_TAG = "villageModVillagerData";

	@Unique
	private UUID villageMod$villageId;

	@Unique
	private VillagerData villageMod$data = new VillagerData();

	@Override
	public Optional<UUID> villageMod$getVillageId() {
		return Optional.ofNullable(this.villageMod$villageId);
	}

	@Override
	public void villageMod$setVillageId(UUID id) {
		if (villageMod$villageId == id) {
			return;
		}

		if (villageMod$villageId != null) {
			var position = ((Villager) (Object) this).position();
			villageMod$LOG.warn(ExcentricCommon.LOG_MARKER, "Overwriting village ID on villager entity at {}",
			    position);
		}
		this.villageMod$villageId = id;
	}

	@Override
	public VillagerData villageMod$getVillagerData() {
		return this.villageMod$data;
	}

	@Override
	public void villageMod$setVillagerData(VillagerData data) {
		this.villageMod$data = data;
	}

	/// Save [VillagerData] to persistent storage.
	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void villageMod$save(CompoundTag tag, CallbackInfo ci) {
		if (villageMod$villageId != null) {
			tag.putUUID(villageMod$ID_TAG, this.villageMod$villageId);
		}
		tag.put(villageMod$DATA_TAG, this.villageMod$data.save());
	}

	/// Load [VillagerData] from persistent storage.
	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void villageMod$load(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(villageMod$ID_TAG)) {
			this.villageMod$villageId = tag.getUUID(villageMod$ID_TAG);
		}
		if (tag.contains(villageMod$DATA_TAG)) {
			this.villageMod$data = VillagerData.load(tag.getCompound(villageMod$DATA_TAG));
		}
	}
}
