package net.nausicaea.excentric.util

sealed interface Try<out T : Any> {
    fun isOk(): Boolean
    fun isErr(): Boolean

    @JvmInline
    value class Err(val value: Throwable): Try<Nothing> {
        override fun isOk(): Boolean = false

        override fun isErr(): Boolean = true
    }

    @JvmInline
    value class Ok<U : Any>(val value: U): Try<U> {
        override fun isOk(): Boolean = true

        override fun isErr(): Boolean = false
    }
}

fun <T : Any> Try<T>.ok(): Opt<T> {
    return when (this) {
        is Try.Ok -> this.value.asOpt
        is Try.Err -> Opt(null)
    }
}
