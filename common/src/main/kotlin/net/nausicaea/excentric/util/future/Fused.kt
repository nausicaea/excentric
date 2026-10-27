package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.Pending
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Ready

class Fused<out T : Any>(
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
}
