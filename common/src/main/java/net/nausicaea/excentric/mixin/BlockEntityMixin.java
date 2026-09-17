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
	/// Always leave empty
	@Inject(method = "saveAdditional", at = @At("TAIL"))
	protected void villageMod$save(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {}

	/// Always leave empty
	@Inject(method = "loadAdditional", at = @At("TAIL"))
	protected void villageMod$load(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {}
}
