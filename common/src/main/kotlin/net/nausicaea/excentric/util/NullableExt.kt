package net.nausicaea.excentric.util

import java.util.Optional

val <T> T?.isSome get() = this != null

val <T> T?.isNone get() = this == null

inline fun <T, U> T?.map(f: (T) -> U): U? = this?.let(f)

inline fun <T, U> T?.andThen(f: (T) -> U?): U? = this?.let(f)

fun <T> T?.or(other: T?): T? = this ?: other

inline fun <T> T?.orElse(f: () -> T?): T? = this ?: f()

inline fun <T> T?.filter(predicate: (T) -> Boolean): T? =
    this?.let { if (predicate(it)) this else null }

inline fun <T> T?.forEach(f: (T) -> Unit) = this?.let(f)

fun <T> T?.unwrapOr(default: T): T = this ?: default

inline fun <T> T?.unwrapOrElse(default: () -> T): T = this ?: default()

fun <T> T?.toOptional(): Optional<T & Any> = Optional.ofNullable(this)
