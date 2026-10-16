package net.nausicaea.excentric.util

fun interface Future<out T : Any> {
    fun poll(): Poll<T>
}
