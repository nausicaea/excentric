package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.Err
import net.nausicaea.excentric.util.Ok
import net.nausicaea.excentric.util.Pending
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Ready
import net.nausicaea.excentric.util.Try

class Cancellable<out T : Any>(
    private var fut: Future<T>?,
    private val cancelFn: () -> Boolean,
) : FusedFuture<Try<T>> {
    override fun poll(): Poll<Try<T>> {
        if (cancelFn()) {
            fut = null
            return Ready(
                Err(
                    Cancelled(),
                ),
            )
        }
        return when (val poll = fut?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                Ready(
                    Ok(poll.value),
                )
            }
        }
    }

    override fun isTerminated(): Boolean = fut == null
}
