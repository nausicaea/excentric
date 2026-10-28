package net.nausicaea.excentric.ai.service

import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.FusedFuture
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.PollAfterReady
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready
import net.nausicaea.excentric.util.task.mapOk

abstract class ServiceFuture(
    private var prq: Future<Try<Unit>>?,
) : FusedFuture<Try<Probability>> {
    protected abstract fun hasExpired(): Boolean

    protected abstract fun useNow(): Probability

    override fun isTerminated(): Boolean = prq == null

    override fun close() {
        prq?.close()
        prq = null
    }

    override fun poll(): Poll<Try<Probability>> {
        if (hasExpired()) {
            close()
            return Ready(Err(ServiceExpired()))
        }
        return prq?.poll()?.mapOk {
            close()
            useNow()
        } ?: throw PollAfterReady()
    }
}
