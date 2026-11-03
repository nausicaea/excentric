package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ai.NeedsCollection
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.minecraft.core.RESTEDNESS
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class SleepFuture(
    val subject: Sleeper,
    val needs: NeedsCollection,
    val need: ResourceKey<Need> = NEED_REGISTRY.getResourceKey(RESTEDNESS).get(),
    val increment: Probability,
    val pos: BlockPos,
) : Future<Try<Probability>> {
    private var initialised: Boolean = false

    @Suppress("ReturnCount")
    override fun poll(): Poll<Try<Probability>> {
        if (!initialised) {
            if (subject.isSleeping) {
                return Ready(Err(AlreadyAsleep()))
            }
            subject.startSleeping(pos)
            initialised = true
        }
        if (subject.isSleeping) {
            needs.realise(need, increment)

            if (needs[need]?.isApproxOne ?: false) {
                subject.stopSleeping()
                return Ready(Ok(Probability.ZERO))
            }
        }
        return Pending
    }

    override fun close() {
        if (initialised && subject.isSleeping) {
            subject.stopSleeping()
        }
    }
}
