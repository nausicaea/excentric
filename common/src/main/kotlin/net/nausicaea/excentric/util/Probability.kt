package net.nausicaea.excentric.util

import kotlin.math.expm1

@ConsistentCopyVisibility
data class Probability private constructor(
    var value: Double,
) {
    operator fun plus(rhs: Probability): Probability = of(value + rhs.value)

    operator fun times(rhs: Probability) = of(value * rhs.value)

    fun inv() = of(1.0 - value)

    val isSubnormal: Boolean get() = value < java.lang.Double.MIN_NORMAL
    val isNonZero: Boolean get() = !isSubnormal

    companion object {
        @JvmField val ZERO = Probability(0.0)

        @JvmField val ONE = Probability(1.0)

        @JvmStatic
        fun of(value: Double) = Probability(value.coerceIn(0.0, 1.0))

        @JvmStatic
        fun mean(src: Collection<Probability>): Probability = if (src.isEmpty()) ZERO else of(src.sumOf { it.value } / src.size)

        @JvmStatic
        fun boltzmann(
            utility: Probability,
            temperature: Probability,
        ) = of(expm1(utility.value / temperature.value))
    }
}
