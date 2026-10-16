package net.nausicaea.excentric.util

/**
 * Creates a sequence that both filters and maps to a new value.
 *
 * The returned sequence yields only values for which the supplied lambda returns a non-null value.
 */
inline fun <T : Any, U : Any> Sequence<T>.filterMap(crossinline f: (T) -> U?): Sequence<U> = flatMap { toSeq(f(it)) }
