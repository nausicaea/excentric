package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class AndThenOk<out T : Any, out U : Any>(
    private var a: Future<Try<T>>?,
    private val f: (T) -> Future<Try<U>>,
    private var bFut: Future<Try<U>>? = null,
) : FusedFuture<Try<U>> {
    override fun isTerminated(): Boolean = a == null && bFut == null

    override fun poll(): Poll<Try<U>> {
        when (val aPoll = a?.poll()) {
            is Pending -> {
                return Pending
            }

            is Ready -> {
                when (val aTry = aPoll.value) {
                    is Err -> {
                        a = null
                        return Ready(
                            Err(
                                aTry.value,
                            ),
                        )
                    }

                    is Ok -> {
                        bFut = f(aTry.value)
                        a = null
                    }
                }
            }

            null -> {}
        }
        return when (val bPoll = bFut?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                when (val bTry = bPoll.value) {
                    is Err -> {
                        bFut = null
                        Ready(
                            Err(
                                bTry.value,
                            ),
                        )
                    }

                    is Ok -> {
                        bFut = null
                        Ready(
                            Ok(
                                bTry.value,
                            ),
                        )
                    }
                }
            }
        }
    }

    override fun close() {
        a?.close()
        a = null
        bFut?.close()
        bFut = null
    }
}
