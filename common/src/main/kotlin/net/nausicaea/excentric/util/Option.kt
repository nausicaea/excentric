package net.nausicaea.excentric.util

import java.util.Optional

sealed interface Option<out T> {
    companion object {
        fun <T> of(value: T?): Option<T> =
            if (value == null) {
                None
            } else {
                Some(value)
            }
    }

    fun isNone(): Boolean

    fun isSome(): Boolean

    fun toOptional(): Optional<out T>

    fun unwrap(): T

    object None : Option<Nothing> {
        override fun isNone(): Boolean = true

        override fun isSome(): Boolean = false

        override fun toOptional(): Optional<out Nothing> = Optional.empty()

        override fun unwrap(): Nothing = throw NoSuchElementException("None")
    }

    @JvmInline
    value class Some<out T : Any>(
        private val value: T,
    ) : Option<T> {
        override fun isNone(): Boolean = false

        override fun isSome(): Boolean = true

        override fun toOptional(): Optional<out T> = Optional.of(value)

        override fun unwrap(): T = value
    }
}

fun <T> Option<T>.unwrapOr(default: T): T =
    if (this.isNone()) {
        default
    } else {
        this.unwrap()
    }

fun <T> Option<T>.unwrapOrNull(): T? =
    if (this.isNone()) {
        null
    } else {
        this.unwrap()
    }

fun <T> Option<T>.unwrapOrElse(defaultFn: () -> T): T =
    if (this.isNone()) {
        defaultFn()
    } else {
        this.unwrap()
    }

fun <T> Option<T>.filter(predicate: (T) -> Boolean): Option<T> =
    if (isSome() && predicate(unwrap())) {
        this
    } else {
        Option.None
    }

fun <T> Option<T>.orElse(f: () -> Option<T>): Option<T> =
    if (isNone()) {
        f()
    } else {
        this
    }

fun <T, U> Option<T>.andThen(f: (T) -> Option<U>): Option<U> =
    if (isSome()) {
        f(unwrap())
    } else {
        Option.None
    }

fun <T, U : Any> Option<T>.map(f: (T) -> U): Option<U> =
    if (isSome()) {
        Option.Some(f(unwrap()))
    } else {
        Option.None
    }

fun <T> Option<T>.forEach(f: (T) -> Unit) {
    if (isSome()) {
        f(unwrap())
    }
}
