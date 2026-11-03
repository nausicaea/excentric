package net.nausicaea.excentric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import kotlin.Unit;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.ai.navigation.Navigator;
import net.nausicaea.excentric.ai.prerequisite.Position;
import net.nausicaea.excentric.minecraft.world.entity.ai.navigation.PathNavigationFut;
import net.nausicaea.excentric.util.future.FusedFuture;
import net.nausicaea.excentric.util.result.Try;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
abstract class MobMixin implements Navigator {
    /// [Villager]s shall ignore the mob griefing game rule (`GameRules#RULE_MOBGRIEFING`): if it's
    /// set to false (i.e. disallows mobs from modifying the world), Villagers have an exemption.
    @ModifyExpressionValue(
            method = "aiStep()V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    public boolean villageMod$mobGriefingException(boolean original) {
        return original || ((Object) this instanceof Villager);
    }

    @Override
    public @NotNull FusedFuture<@NotNull Try<@NotNull Unit>> villageMod$navigateTo(
            @NotNull Position pos) {
        return new PathNavigationFut(pos, ((Mob) (Object) this).getNavigation());
    }
}
