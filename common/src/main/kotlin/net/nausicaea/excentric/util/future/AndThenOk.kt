package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class AndThenOk<T : Any, U : Any>(
    private var first: Future<Try<T>>?,
    private val f: (T) -> Future<Try<U>>,
) : FusedFuture<Try<U>> {
    private var second: Future<Try<U>>? = null

    override fun isTerminated(): Boolean = first == null && second == null

    override fun poll(): Poll<Try<U>> {
        when (val firstPoll = first?.poll()) {
            is Pending -> {
                return Pending
            }

            is Ready -> {
                when (val firstTry = firstPoll.value) {
                    is Err -> {
                        closeFirst()
                        return Ready(
                            Err(
                                firstTry.value,
                            ),
                        )
                    }

                    is Ok -> {
                        second = f(firstTry.value)
                        closeFirst()
                    }
                }
            }

            null -> {}
        }
        return when (val secondPoll = second?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                when (val secondTry = secondPoll.value) {
                    is Err -> {
                        closeSecond()
                        Ready(
                            Err(
                                secondTry.value,
                            ),
                        )
                    }

                    is Ok -> {
                        closeSecond()
                        Ready(
                            Ok(
                                secondTry.value,
                            ),
                        )
                    }
                }
            }
        }
    }

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
