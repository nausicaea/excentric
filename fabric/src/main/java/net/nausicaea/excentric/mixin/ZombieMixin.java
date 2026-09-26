package net.nausicaea.excentric.mixin;

import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.nausicaea.excentric.mixin.accessor.MobAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Zombie.class)
abstract class ZombieMixin {
	/**
	 * Remove the target selectors for villagers and iron golems.
	 *
	 * @author developer@nausicaea.net
	 * @reason Don't let zombies attack villagers and golems.
	 */
	@Overwrite
	public void addBehaviourGoals() {
		var zmb = (Zombie) (Object) this;
		var mob = (MobAccessor) this;
		var goalSelector = mob.villageMod$getGoalSelector();
		goalSelector.addGoal(2, new ZombieAttackGoal(zmb, 1.0, false));
		goalSelector.addGoal(6, new MoveThroughVillageGoal(zmb, 1.0, true, 4, zmb::canBreakDoors));
		goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(zmb, 1.0));
		var targetSelector = mob.villageMod$getTargetSelector();
		targetSelector.addGoal(1, new HurtByTargetGoal(zmb).setAlertOthers(ZombifiedPiglin.class));
		targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(zmb, Player.class, true));
		targetSelector.addGoal(5,
		    new NearestAttackableTargetGoal<>(zmb, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
	}
}
