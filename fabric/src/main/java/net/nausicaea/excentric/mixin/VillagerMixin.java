package net.nausicaea.excentric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import net.nausicaea.excentric.EveryN;
import net.nausicaea.excentric.minecraft.world.entity.ai.NeedsCollection;
import net.nausicaea.excentric.minecraft.world.entity.ai.NeedsContext;
import net.nausicaea.excentric.minecraft.world.entity.ai.NeedsReasoner;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Needs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
abstract class VillagerMixin {
	@Unique
	private final NeedsCollection villageMod$needs = new NeedsCollection();
	@Unique
	private final NeedsReasoner villageMod$reasoner = new NeedsReasoner();
	@Unique
	private EveryN villageMod$every32Ticks;
	@Unique
	private EveryN villageMod$every8Ticks;
	@Unique
	private RandomSource villageMod$rng;

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/npc/VillagerType;)V", at = @At("TAIL"))
	public void villageMod$constructor(EntityType<? extends Villager> entityType, Level level,
	    VillagerType villagerType, CallbackInfo ci) {
		villageMod$every32Ticks = new EveryN(32, level.random.nextInt(32));
		villageMod$every8Ticks = new EveryN(8, level.random.nextInt(8));
		villageMod$rng = level.random.fork();

		// Add initial needs
		// TODO: make need assignment data-driven
		villageMod$needs.add(Needs.SATIATION);
	}

	/// [Villager]s shall not use their [Brain]: concretely, `Brain#tick()` may not
	/// be called on villagers during the AI ticking call.
	@WrapWithCondition(method = "customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/Brain;tick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
	public boolean villageMod$abortVanillaVillagerAi(Brain<Villager> instance, ServerLevel serverLevel,
	    LivingEntity livingEntity) {
		return false;
	}

	@Inject(method = "tick()V", at = @At("TAIL"))
	public void villageMod$tickTail(CallbackInfo ci) {
		if (!(((Villager) (Object) this).level() instanceof ServerLevel level)) {
			// We don't run this on the client.
			return;
		}

		var gameTime = level.getGameTime();
		var ctx = new NeedsContext(villageMod$needs, level);
		villageMod$reasoner.tryRealiseService(ctx);
		villageMod$every8Ticks.run(() -> villageMod$needs.decay(gameTime));
		villageMod$every32Ticks.run(() -> villageMod$reasoner.plan(ctx, villageMod$rng));
	}
}
