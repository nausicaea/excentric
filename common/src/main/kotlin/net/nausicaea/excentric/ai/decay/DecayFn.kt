package net.nausicaea.excentric.ai.decay

import net.nausicaea.excentric.util.Probability

interface DecayFn {
    /**
     * Reduce the `value` for the given time window.
     */
    fun decay(
        value: Probability,
        deltaTime: Double,
    ): Probability
}
