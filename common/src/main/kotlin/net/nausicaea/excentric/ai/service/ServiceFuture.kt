package net.nausicaea.excentric.ai.service

import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.PollAfterReady
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.map

abstract class ServiceFuture(
    val prq: Future<Try<Unit>>,
) : Future<Try<Probability>> {
    private var isReady: Boolean = false

    protected abstract fun hasExpired(): Boolean

    protected abstract fun useNow(): Probability

    override fun poll(): Poll<Try<Probability>> {
        if (isReady) {
            throw PollAfterReady()
        }
        if (hasExpired()) {
            isReady = true
            // TODO: this case should also abort pathfinding.
            return Poll.Ready(Try.Err(ServiceExpired()))
        }
        return prq
            .poll()
            .map {
                isReady = true
                Try.Ok(useNow())
            }
    }
}
