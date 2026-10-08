package net.nausicaea.excentric.ai.decay

import net.nausicaea.excentric.java.util.Probability

interface DecayFn {
    /**
     * Reduce the `value` for the given time window.
     */
    fun decay(
        value: Probability,
        deltaTime: Double,
    ): Probability
}
