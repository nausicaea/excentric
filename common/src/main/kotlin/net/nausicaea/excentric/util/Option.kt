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
}

object None : Option<Nothing> {
    override fun isNone(): Boolean = true

    override fun isSome(): Boolean = false

    override fun toOptional(): Optional<out Nothing> = Optional.empty()
}

@JvmInline
value class Some<out T : Any>(
    private val value: T,
) : Option<T> {
    override fun isNone(): Boolean = false

    override fun isSome(): Boolean = true

    override fun toOptional(): Optional<out T> = Optional.of(value)
}
