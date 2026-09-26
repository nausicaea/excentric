package net.nausicaea.excentric.mixin;

import net.minecraft.world.entity.monster.Drowned;
import net.nausicaea.excentric.MobTargetingUtils;
import net.nausicaea.excentric.mixin.accessor.MobAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Drowned.class)
abstract class DrownedMixin {
	@Inject(method = "addBehaviourGoals()V", at = @At("TAIL"))
	public void addBehaviourGoals(CallbackInfo ci) {
		var mob = (MobAccessor) this;
		var targetSelector = mob.villageMod$getTargetSelector();
		MobTargetingUtils.removeVillagerAndGolemTargeting(targetSelector);
	}
}
