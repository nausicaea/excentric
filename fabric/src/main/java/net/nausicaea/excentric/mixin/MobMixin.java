package net.nausicaea.excentric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
abstract class MobMixin {
	/// [Villager]s shall ignore the mob griefing game rule
	/// (`GameRules#RULE_MOBGRIEFING`): if it's set to false (i.e. disallows mobs
	/// from modifying the world), Villagers have an exemption.
	@ModifyExpressionValue(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
	public boolean villageMod$mobGriefingException(boolean original) {
		return original || ((Object) this instanceof Villager);
	}
}
