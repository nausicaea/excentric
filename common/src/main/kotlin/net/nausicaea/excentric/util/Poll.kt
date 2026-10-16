package net.nausicaea.excentric.util

import net.nausicaea.excentric.util.Opt.Companion.None

/**
 * Indicates whether a value is available or still awaiting completion.
 *
 * This is returned by [Future.poll].
 */
sealed interface Poll<out T : Any> {
    /**
     * Return `true` if the polled result is still pending.
     */
    fun isPending(): Boolean

    /**
     * Return `true` if the polled result is available.
     */
    fun isReady(): Boolean

    /**
     * Represents that a value is not ready yet.
     */
    object Pending : Poll<Nothing> {
        override fun isPending(): Boolean = true

        override fun isReady(): Boolean = false
    }

    /**
     * Represents that a value is immediately ready.
     */
    data class Ready<V : Any>(
        val value: V,
    ) : Poll<V> {
        override fun isPending(): Boolean = false

        override fun isReady(): Boolean = true
    }
}

/**
 * Return an [Opt] containing the polled result. If a result is available with [Poll.Ready], return [Opt.Some], otherwise return [None].
 */
fun <T : Any> Poll<T>.ready(): Opt<T> =
    when (this) {
        is Poll.Ready -> this.value.asOpt()
        is Poll.Pending -> None
    }

/**
 * If a value is ready with [Poll.Ready], call the function `f` to peek into the value, then return the original [Poll] value.
 */
inline fun <T : Any> Poll<T>.inspect(f: (T) -> Unit): Poll<T> =
    when (this) {
        is Poll.Ready -> {
            f(value)
            this
        }

        is Poll.Pending -> {
            this
        }
    }

/**
 * Map a [Poll.Ready] value with the function `f` to a new value or return [Poll.Pending] without calling the function if no value is ready.
 */
inline fun <T : Any, U : Any> Poll<T>.map(f: (T) -> U): Poll<U> =
    when (this) {
        is Poll.Ready -> Poll.Ready(f(value))
        is Poll.Pending -> this
    }
