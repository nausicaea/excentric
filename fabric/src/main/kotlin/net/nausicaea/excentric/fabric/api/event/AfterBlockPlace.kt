package net.nausicaea.excentric.fabric.api.event

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState

fun interface AfterBlockPlace {
    fun afterBlockPlace(
        prevState: BlockState,
        pos: BlockPos,
        level: ServerLevel,
    )
}
