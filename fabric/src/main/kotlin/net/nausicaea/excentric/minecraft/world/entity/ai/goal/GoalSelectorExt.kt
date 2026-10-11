package net.nausicaea.excentric.minecraft.world.entity.ai.goal

import net.minecraft.world.entity.ai.goal.GoalSelector
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.npc.AbstractVillager
import net.nausicaea.excentric.mixin.accessor.NearestAttackableTargetGoalAccessor

/**
 * Given a [GoalSelector], remove any references to
 * [AbstractVillager] and [IronGolem].
 */
fun GoalSelector.removeVillagerAndGolemTargeting() {
    this.removeAllGoals({ goal ->
        if (goal !is NearestAttackableTargetGoal<*>) {
            return@removeAllGoals false
        }
        val targetType =
            (goal as NearestAttackableTargetGoalAccessor)
                .`villageMod$getTargetType`()
        targetType == AbstractVillager::class.java || targetType == IronGolem::class.java
    })
}
