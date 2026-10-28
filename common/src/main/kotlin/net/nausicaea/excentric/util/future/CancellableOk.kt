package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class CancellableOk<out T : Any>(
    private var fut: Future<Try<T>>?,
    private val cancelFn: () -> Boolean,
) : FusedFuture<Try<T>> {
    override fun poll(): Poll<Try<T>> {
        if (cancelFn()) {
            close()
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
                when (val result = poll.value) {
                    is Err -> Ready(Err(result.value))
                    is Ok -> Ready(Ok(result.value))
                }
            }
        }
    }

    override fun isTerminated(): Boolean = fut == null

    override fun close() {
        fut?.close()
        fut = null
    }
}
