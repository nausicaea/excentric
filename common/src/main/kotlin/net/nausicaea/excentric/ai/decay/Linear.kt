package net.nausicaea.excentric.ai.decay

import net.nausicaea.excentric.util.Probability

data class Linear(
    val baseRate: Double,
) : DecayFn {
    override fun decay(
        value: Probability,
        deltaTime: Double,
    ): Probability = Probability.of(value.value - baseRate * deltaTime)
}
