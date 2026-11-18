package net.nausicaea.excentric.minecraft.world.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.ai.service.Sleeper

data class LivingEntitySleeper(
    val entity: LivingEntity,
) : Sleeper {
    override val isSleeping: Boolean
        get() = entity.isSleeping

    override fun startSleeping(pos: BlockPos) = entity.startSleeping(pos)

    override fun stopSleeping() = entity.stopSleeping()
}
