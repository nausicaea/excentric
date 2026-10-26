package net.nausicaea.excentric.util

import java.util.Optional
import kotlin.jvm.optionals.getOrNull

/**
 * Type [Opt] represents an optional value: every [Opt] is either [Some] and contains a value, or [None], and does not.
 *
 * Option types have a number of uses:
 *
 * - Initial values
 * - Return values for functions that are not defined over their entire input range (partial functions)
 * - Return value for otherwise reporting simple errors, where None is returned on error
 * - Optional class fields
 * - Class fields that can be loaned or “taken”
 * - Optional function arguments
 * - Nullable values
 * - Swapping things out of difficult situations
 *
 * However: In this implementation, they cannot be used for exhaustive matching using `when` expressions.
 */
@JvmInline
value class Opt<out T : Any>(
    /**
     * Returns the contained value or `null`, converting the wrapped value to a nullable one.
     */
    val unwrapOrNull: T?,
) {
    /**
     * Return `true` if the [Opt] value is not null (i.e. [Some])
     */
    val isSome: Boolean get() = unwrapOrNull != null

    /**
     * Return `true` if the [Opt] value is null (i.e. [None])
     */
    val isNone: Boolean get() = unwrapOrNull == null

    /**
     * Map an `Opt<T>` to an `Opt<U>` by applying a function only if the value is present (i.e. [Some]). Return [None] otherwise.
     */
    inline fun <U : Any> map(f: (T) -> U): Opt<U> = Opt(unwrapOrNull?.let(f))

    /**
     * Map an `Opt<T>` to an `Opt<U>` by applying a function only if the value is present (i.e. [Some]). Return [None] otherwise.
     * The mapping function may also return [None]. Some languages call this function flatmap.
     */
    inline fun <U : Any> andThen(f: (T) -> Opt<U>): Opt<U> = unwrapOrNull?.let(f) ?: Opt(null)

    /**
     * Calls a function with a reference to the contained value if [Some].
     *
     * Returns the original option.
     */
    inline fun inspect(f: (T) -> Unit): Opt<T> {
        unwrapOrNull?.let(f)
        return this
    }

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
        val v = unwrapOrNull
        return if (v != null && predicate(v)) this else Opt(null)
    }

    inline fun forEach(f: (T) -> Unit) {
        unwrapOrNull?.let(f)
    }
}

/**
 * No value.
 */
val None: Opt<Nothing> = Opt(null)

/**
 * Some value of type `T`.
 */
@Suppress("FunctionName")
fun <T : Any> Some(v: T) = Opt(v)

/**
 * Returns the option if it contains a value, otherwise returns `other`.
 *
 * Arguments passed to [or] are eagerly evaluated; if you are passing the result of a function call, it is recommended to use [orElse], which is lazily evaluated.
 */
fun <T : Any> Opt<T>.or(other: Opt<T>): Opt<T> = if (isSome) this else other

/**
 * Returns the option if it contains a value, otherwise calls `f` and returns the result.
 */
inline fun <T : Any> Opt<T>.orElse(f: () -> Opt<T>): Opt<T> = if (isSome) this else f()

/**
 * Returns the contained Some value or a provided default.
 *
 * Arguments passed to [unwrapOr] are eagerly evaluated; if you are passing the result of a function call, it is recommended to use [unwrapOrElse], which is lazily evaluated.
 */
fun <T : Any> Opt<T>.unwrapOr(default: T): T = unwrapOrNull ?: default

/**
 * Returns the contained [Opt.Companion.Some] value or computes it from a closure.
 */
inline fun <T : Any> Opt<T>.unwrapOrElse(default: () -> T): T = unwrapOrNull ?: default()

/**
 * Convert an [Opt] value to an [Optional]. This involves a nullability check.
 */
fun <T : Any> Opt<T>.toOptional(): Optional<T> = Optional.ofNullable(unwrapOrNull)

/**
 * Convert a nullable value to an [Opt]-wrapped value. The prefix `as-` indicates that this is a cheap conversion.
 */
fun <T : Any> T?.asOpt(): Opt<T> = Opt(this)

/**
 * Convert an [Optional] value to an [Opt]. This involves a nullability check.
 */
fun <T : Any> Optional<T>.toOpt(): Opt<T> = this.getOrNull().asOpt()

/**
 * Convert an [Opt] value to a [Sequence] with either one or no value.
 */
fun <T : Any> Opt<T>.toSequence(): Sequence<T> = toSeq(unwrapOrNull)

/**
 * Helper method to convert a nullable value to a [Sequence].
 */
fun <T : Any> toSeq(value: T?): Sequence<T> = value?.let { sequenceOf(it) } ?: emptySequence()

/**
 * Attempt a wrapped cast to a different type.
 */
inline fun <reified U : Any> Opt<*>.tryCast(): Opt<U> = Opt(unwrapOrNull as? U)
