package net.nausicaea.excentric.ai.service

import net.nausicaea.excentric.util.Err
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Ok
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.PollAfterReady
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Ready
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.map

abstract class ServiceFuture(
    val prq: Future<Try<Unit>>,
    private var isReady: Boolean = false,
) : Future<Try<Probability>> {
    protected abstract fun hasExpired(): Boolean

    protected abstract fun useNow(): Probability

    override fun poll(): Poll<Try<Probability>> {
        if (isReady) {
            throw PollAfterReady()
        }
        if (hasExpired()) {
            isReady = true
            return Ready(Err(ServiceExpired()))
        }
        return prq
            .poll()
            .map {
                isReady = true
                Ok(useNow())
            }
    }
}
