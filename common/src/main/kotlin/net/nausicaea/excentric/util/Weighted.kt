package net.nausicaea.excentric.util

import net.nausicaea.excentric.java.util.Probability

/**
 * Any item that has an associated probabilistic weight.
 */
data class Weighted<T>(
    val item: T,
    val weight: Probability,
)
