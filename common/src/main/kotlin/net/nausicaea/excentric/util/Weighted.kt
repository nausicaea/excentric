package net.nausicaea.excentric.util

/**
 * Any item that has an associated probabilistic weight.
 */
data class Weighted<out T>(
    val item: T,
    val weight: Probability,
)
