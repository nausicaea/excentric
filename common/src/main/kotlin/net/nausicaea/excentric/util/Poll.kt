package net.nausicaea.excentric.util

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
}

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

/**
 * Return an [Opt] containing the polled result. If a result is available with [Ready], return [Some], otherwise return [None].
 */
fun <T : Any> Poll<T>.ready(): Opt<T> =
    when (this) {
        is Ready -> this.value.asOpt()
        is Pending -> None
    }

/**
 * If a value is ready with [Ready], call the function `f` to peek into the value, then return the original [Poll] value.
 */
inline fun <T : Any> Poll<T>.inspect(f: (T) -> Unit): Poll<T> =
    when (this) {
        is Ready -> {
            f(value)
            this
        }

        is Pending -> {
            this
        }
    }

/**
 * Map a [Ready] value with the function `f` to a new value or return [Pending] without calling the function if no value is ready.
 */
inline fun <T : Any, U : Any> Poll<T>.map(f: (T) -> U): Poll<U> =
    when (this) {
        is Ready -> Ready(f(value))
        is Pending -> this
    }
