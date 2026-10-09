package net.nausicaea.excentric.util

/**
 * Any item that has an associated probabilistic weight.
 */
data class Weighted<T>(
    val item: T,
    val weight: Probability,
)
