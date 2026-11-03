package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class AndThen<T : Any, U : Any>(
    private var first: Future<T>?,
    private val f: (T) -> Future<U>,
) : FusedFuture<U> {
    private var second: Future<U>? = null

    override fun poll(): Poll<U> {
        when (val firstPoll = first?.poll()) {
            is Pending -> {
                return Pending
            }

            is Ready -> {
                second = f(firstPoll.value)
                closeFirst()
            }

            null -> {}
        }
        return when (val secondPoll = second?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                closeSecond()
                Ready(secondPoll.value)
            }
        }
    }

    override fun isTerminated(): Boolean = first == null && second == null

    override fun close() {
        closeFirst()
        closeSecond()
    }

    private fun closeFirst() {
        first?.close()
        first = null
    }

    private fun closeSecond() {
        second?.close()
        second = null
    }
}
