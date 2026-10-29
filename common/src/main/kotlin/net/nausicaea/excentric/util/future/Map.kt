package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class Map<T : Any, U : Any>(
    private var fut: Future<T>?,
    private val f: (T) -> U,
) : Future<U> {
    override fun poll(): Poll<U> =
        when (val aPoll = fut?.poll()) {
            is Pending, null -> {
                Pending
            }

            is Ready -> {
                fut = null
                Ready(f(aPoll.value))
            }
        }

    override fun close() {
        fut?.close()
        fut = null
    }
}
