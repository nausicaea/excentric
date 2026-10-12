package net.nausicaea.excentric.util

sealed interface Try<T> {
    @JvmInline
    value class Ok<U>(val value: U): Try<U>
    @JvmInline
    value class Err(val value: Throwable): Try<Nothing>
}