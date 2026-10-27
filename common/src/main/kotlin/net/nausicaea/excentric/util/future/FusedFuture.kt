package net.nausicaea.excentric.util.future

interface FusedFuture<out T : Any> : Future<T> {
    fun isTerminated(): Boolean
}
