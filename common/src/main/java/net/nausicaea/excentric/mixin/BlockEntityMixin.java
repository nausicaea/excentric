package net.nausicaea.excentric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
abstract class BlockEntityMixin {
	/// Hook into NBT saving to store custom persistent data on [BlockEntity].
	///
	/// **Leave implementation empty: must be overridden by a more specific class**
	@Inject(method = "saveAdditional", at = @At("TAIL"))
	protected void villageMod$save(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
	}

	/// Hook into NBT loading to load custom persistent data from a serialized
	/// [BlockEntity].
	///
	/// **Leave implementation empty: must be overridden by a more specific class**
	@Inject(method = "loadAdditional", at = @At("TAIL"))
	protected void villageMod$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
	}
}
