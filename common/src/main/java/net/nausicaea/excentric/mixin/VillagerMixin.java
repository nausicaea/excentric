package net.nausicaea.excentric.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.VillageRef;
import net.nausicaea.excentric.VillagerData;
import net.nausicaea.excentric.VillagerDataHolder;
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
	private static final String villageMod$TAG = "villageModVillagerData";

	@Unique
	private VillagerData villageMod$data = new VillagerData();

	@Unique
	private UUID villageMod$villageId;

	@Override
	public Optional<UUID> villageMod$getVillageId() {
		return Optional.ofNullable(this.villageMod$villageId);
	}

	@Override
	public void villageMod$setVillageId(UUID id) {
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

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void villageMod$save(CompoundTag tag, CallbackInfo ci) {
		tag.put(villageMod$TAG, this.villageMod$data.save());
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void villageMod$load(CompoundTag tag, CallbackInfo ci) {
		if (tag.contains(villageMod$TAG)) {
			this.villageMod$data = VillagerData.load(tag.getCompound(villageMod$TAG));
		}
	}
}
