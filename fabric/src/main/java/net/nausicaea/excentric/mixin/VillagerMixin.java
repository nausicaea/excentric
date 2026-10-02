package net.nausicaea.excentric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Villager.class)
abstract class VillagerMixin {
	/// [Villager]s shall not use their [Brain]: concretely, `Brain#tick()` may not
	/// be called on villagers during the AI ticking call.
	@WrapWithCondition(method = "customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
	public boolean villageMod$abortVanillaVillagerAi(Brain<Villager> instance, ServerLevel serverLevel,
	    LivingEntity livingEntity) {
		return false;
	}
}
