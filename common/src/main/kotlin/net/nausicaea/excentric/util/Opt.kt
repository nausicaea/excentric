package net.nausicaea.excentric.util

import java.util.Optional
import kotlin.jvm.optionals.getOrNull

/**
 * Wrap nullable values in an Option type to enable functional operations (using method chaining).
 */
@JvmInline
value class Opt<out T : Any>(
    val orNull: T?,
) {
    /**
     * Return `true` if the [Opt] value is not null (i.e. [Some])
     */
    val isSome: Boolean get() = orNull != null

    /**
     * Return `true` if the [Opt] value is null (i.e. [None])
     */
    val isNone: Boolean get() = orNull == null

    /**
     * Map an `Opt<T>` to an `Opt<U>` by applying a function only if the value is present (i.e. [Some]). Return [None] otherwise.
     */
    inline fun <U : Any> map(f: (T) -> U): Opt<U> = Opt(orNull?.let(f))

    /**
     * Map an `Opt<T>` to an `Opt<U>` by applying a function only if the value is present (i.e. [Some]). Return [None] otherwise.
     * The mapping function may also return [None]. Some languages call this function flatmap.
     */
    inline fun <U : Any> andThen(f: (T) -> Opt<U>): Opt<U> = orNull?.let(f) ?: Opt(null)

    /**
     * Returns [None] if the option is [None], otherwise calls `predicate`
     * with the wrapped value and returns:
     *
     * - [Some] containing the wrapped value if `predicate` returns `true`, and
     * - [None] if `predicate` returns `false`.
     *
     * This function works similar to [Sequence.filter]. You can imagine
     * the `Opt<T>` being a sequence over one or zero elements. `filter()`
     * lets you decide which elements to keep.
     */
    inline fun filter(predicate: (T) -> Boolean): Opt<T> {
        val v = orNull
        return if (v != null && predicate(v)) this else Opt(null)
    }

    inline fun forEach(f: (T) -> Unit) {
        orNull?.let(f)
    }

    companion object {
        val None: Opt<Nothing> = Opt(null)

        @Suppress("FunctionName")
        fun <T : Any> Some(v: T) = Opt(v)
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
