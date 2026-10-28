package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class ReadyFut<out T : Any>(
    private var value: T?,
) : FusedFuture<T> {
    override fun poll(): Poll<T> =
        value?.let {
            value = null
            Ready(it)
        } ?: Pending

    override fun isTerminated(): Boolean = value == null

    override fun close() {
        value = null
    }
}
