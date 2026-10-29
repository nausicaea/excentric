package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class Fused<T : Any>(
    private var fut: Future<T>?,
) : FusedFuture<T> {
    override fun poll(): Poll<T> =
        when (val poll = fut?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                fut = null
                poll
            }
        }

    override fun isTerminated(): Boolean = fut == null

    override fun close() {
        fut?.close()
        fut = null
    }
}
