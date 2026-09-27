package net.nausicaea.excentric.mixin;

import net.minecraft.world.entity.monster.Zombie;
import net.nausicaea.excentric.minecraft.world.entity.ai.goal.GoalSelectorUtils;
import net.nausicaea.excentric.mixin.accessor.MobAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/// Don't let [Zombie] target and attack
/// [net.minecraft.world.entity.npc.Villager] and
/// [net.minecraft.world.entity.animal.IronGolem].
@Mixin(Zombie.class)
abstract class ZombieMixin {
	@Inject(method = "addBehaviourGoals()V", at = @At("TAIL"))
	public void addBehaviourGoals(CallbackInfo ci) {
		var mob = (MobAccessor) this;
		var targetSelector = mob.villageMod$getTargetSelector();
		GoalSelectorUtils.removeVillagerAndGolemTargeting(targetSelector);
	}
}
