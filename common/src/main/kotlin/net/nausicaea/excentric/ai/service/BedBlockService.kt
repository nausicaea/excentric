package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.BedBlock
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.RESTEDNESS
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.FutureExt.andThenOk
import net.nausicaea.excentric.util.future.FutureExt.cancellableOk
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

data class BedBlockService(
    val pos: BlockPos,
    val ctx: BlockServiceContext,
) : Service {
    override fun key(): ResourceKey<Need> =
        NEED_REGISTRY
            .getResourceKey(RESTEDNESS)
            .orElseThrow {
                IllegalStateException("Cannot find resource key for builtin need 'restedness'")
            }

    /**
     * TODO: make a bed's base utility depend on the occupation or reservation status
     */
    override fun baseUtility(): Probability = Probability.ONE

    override fun prerequisite(): Prerequisite = Position(pos.center)

    override fun describe(): String = "bed at ${pos.toShortString()}"

    override fun apply(): Future<Try<Probability>> {
        val level = ctx.consumer.level()
        val need = key()
        val increment = Probability.of(0.001)
        return prerequisite()
            .fulfil(ctx.consumer)
            .cancellableOk { level.getBlockState(pos).block !is BedBlock }
            .andThenOk { SleepFuture(ctx, need, increment, pos) }
            .fuse()
    }
}

class SleepFuture(
    val ctx: BlockServiceContext,
    val need: ResourceKey<Need>,
    val increment: Probability,
    val pos: BlockPos,
    private var initialised: Boolean = false,
) : Future<Try<Probability>> {
    override fun poll(): Poll<Try<Probability>> {
        if (!initialised) {
            if (ctx.consumer.isSleeping) {
                return Ready(Err(AlreadyAsleep()))
            }
            ctx.consumer.startSleeping(pos)
            initialised = true
        }
        if (ctx.consumer.isSleeping) {
            ctx.needs.realise(need, increment)

            if (ctx.needs[need]?.isApproxOne ?: false) {
                ctx.consumer.stopSleeping()
                return Ready(Ok(Probability.ZERO))
            }
        }
        return Pending
    }

    override fun close() {
        if (initialised && ctx.consumer.isSleeping) {
            ctx.consumer.stopSleeping()
        }
    }
}

class AlreadyAsleep : Exception("cannot go to sleep again: this isn't inception")
