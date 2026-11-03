package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ai.NeedsCollection
import net.nausicaea.excentric.ai.need.MissingNeed
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.minecraft.core.RESTEDNESS
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Err
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
    override fun poll(): Poll<Try<Probability>> {
        if (!needs.containsKey(need)) {
            return Ready(Err(MissingNeed()))
        }
        if (subject.isSleeping) {
            return Ready(Err(AlreadyAsleep()))
        }
        return Pending
    }

    override fun close() {
    }
}
