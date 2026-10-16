package net.nausicaea.excentric.util

sealed interface Try<out T : Any> {
    fun isOk(): Boolean

    fun isErr(): Boolean

    data class Err(
        val value: Throwable,
    ) : Try<Nothing> {
        override fun isOk(): Boolean = false

        override fun isErr(): Boolean = true
    }

    data class Ok<U : Any>(
        val value: U,
    ) : Try<U> {
        override fun isOk(): Boolean = true

        override fun isErr(): Boolean = false
    }
}

/**
 * Converts `Try<T>` to `Opt<T>`, discarding the error: thus, [Try.Ok] will become [Opt.Some] and [Try.Err] will become [Opt.None].
 */
fun <T : Any> Try<T>.ok(): Opt<T> =
    when (this) {
        is Try.Ok -> this.value.asOpt()
        is Try.Err -> Opt(null)
    }

/**
 * Calls a function with a reference to the contained value if [Try.Ok].
 *
 * Returns the original result.
 */
inline fun <T : Any> Try<T>.inspect(f: (T) -> Unit): Try<T> =
    when (this) {
        is Try.Ok -> {
            f(value)
            this
        }

        is Try.Err -> {
            this
        }
    }

/**
 * Maps a `Try<T>` to `Try<U>` by applying a function to a contained [Try.Ok] value, leaving a [Try.Err] value untouched.
 *
 * This function can be used to compose the results of two functions.
 */
inline fun <T : Any, U : Any> Try<T>.map(f: (T) -> U): Try<U> =
    when (this) {
        is Try.Ok -> Try.Ok(f(value))
        is Try.Err -> this
    }
