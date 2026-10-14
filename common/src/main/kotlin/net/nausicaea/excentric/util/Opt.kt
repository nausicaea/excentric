package net.nausicaea.excentric.util

import java.util.Optional
import kotlin.jvm.optionals.getOrNull

@JvmInline
value class Opt<out T : Any>(
    val orNull: T?,
) {
    val isSome: Boolean get() = orNull != null
    val isNone: Boolean get() = orNull == null

    inline fun <U : Any> map(f: (T) -> U): Opt<U> = Opt(orNull?.let(f))

    inline fun <U : Any> andThen(f: (T) -> Opt<U>): Opt<U> = orNull?.let(f) ?: Opt(null)

    inline fun filter(predicate: (T) -> Boolean): Opt<T> {
        val v = orNull
        return if (v != null && predicate(v)) this else Opt(null)
    }

    inline fun forEach(f: (T) -> Unit) {
        orNull?.let(f)
    }
}

fun <T : Any> Opt<T>.or(other: Opt<T>): Opt<T> = if (isSome) this else other

inline fun <T : Any> Opt<T>.orElse(f: () -> Opt<T>): Opt<T> = if (isSome) this else f()

fun <T : Any> Opt<T>.unwrapOr(default: T): T = orNull ?: default

inline fun <T : Any> Opt<T>.unwrapOrElse(default: () -> T): T = orNull ?: default()

fun <T : Any> Opt<T>.toOptional(): Optional<T> = Optional.ofNullable(orNull)

/**
 * Convert a nullable type to an [Opt] wrapper type. This is a cheap operation and is expected to be inlined.
 */
val <T : Any> T?.asOpt: Opt<T> get() = Opt(this)

/**
 * Convert a [Optional] type to an [Opt]. This involves a nullability check and is slightly more expensive. That's why it's a function instead of a property: a function suggests work is being done.
 */
fun <T : Any> Optional<T>.toOpt(): Opt<T> = this.getOrNull().asOpt

fun <T : Any> Opt<T>.toSequence(): Sequence<T> = toSeq(orNull)

fun <T : Any> toSeq(value: T?): Sequence<T> = value?.let { sequenceOf(it) } ?: emptySequence()
