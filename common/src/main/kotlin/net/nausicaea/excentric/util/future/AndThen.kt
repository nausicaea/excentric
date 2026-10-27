package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.Pending
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Ready

class AndThen<out T : Any, out U : Any>(
    private var a: Future<T>?,
    private val f: (T) -> Future<U>,
    private var bFut: Future<U>? = null,
) : FusedFuture<U> {
    override fun poll(): Poll<U> {
        when (val aPoll = a?.poll()) {
            is Pending -> {
                return Pending
            }

            is Ready -> {
                bFut = f(aPoll.value)
                a = null
            }

            null -> {}
        }
        return when (val bPoll = bFut?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                bFut = null
                Ready(bPoll.value)
            }
        }
    }

    override fun isTerminated(): Boolean = a == null && bFut == null
}
