package net.nausicaea.excentric.java.util.stream

import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream

fun <T> Stream<T>.enumerate(): Stream<Enumerated<T>> {
    val index = AtomicInteger()
    return this.map<Enumerated<T>> { item: T -> Enumerated(index.getAndIncrement(), item) }
}

data class Enumerated<T>(
    val index: Int,
    val value: T,
)
