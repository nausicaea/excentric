package net.nausicaea.excentric.util

sealed interface Poll<out T : Any> {
    fun isPending(): Boolean
    fun isReady(): Boolean

    object Pending : Poll<Nothing> {
        override fun isPending(): Boolean = true

        override fun isReady(): Boolean = false
    }

    @JvmInline
    value class Ready<V : Any>(val value: V): Poll<V> {
        override fun isPending(): Boolean = false

        override fun isReady(): Boolean = true
    }
}

fun <T: Any> Poll<T>.ready(): Opt<T> {
    return when (this) {
        is Poll.Ready -> this.value.asOpt
        is Poll.Pending -> Opt(null)
    }
}
