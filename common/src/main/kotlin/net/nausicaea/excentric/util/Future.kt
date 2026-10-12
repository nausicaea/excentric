package net.nausicaea.excentric.util

fun interface Future<T> {
    fun poll(): Poll<out T>
}