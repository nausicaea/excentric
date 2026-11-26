package net.nausicaea.excentric.util.future

interface FusedFuture<T : Any> : Future<T> {
    fun isTerminated(): Boolean
}
