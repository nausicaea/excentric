package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos

interface Sleeper {
    val isSleeping: Boolean

    fun startSleeping(pos: BlockPos)

    fun stopSleeping()
}
