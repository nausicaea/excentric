package net.nausicaea.excentric;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.nausicaea.excentric.mixin.accessor.NearestAttackableTargetGoalAccessor;

public final class MobTargetingUtils {
	/// Given a [GoalSelector], remove any references to
	/// [AbstractVillager] and [IronGolem].
	public static void removeVillagerAndGolemTargeting(GoalSelector targetSelector) {
		targetSelector.removeAllGoals(goal -> {
			if (!(goal instanceof NearestAttackableTargetGoal<?> targetGoal)) {
				return false;
			}
			var targetType = ((NearestAttackableTargetGoalAccessor) targetGoal).villageMod$getTargetType();
			return targetType == AbstractVillager.class || targetType == IronGolem.class;
		});
	}

	private MobTargetingUtils() {
	}
}
