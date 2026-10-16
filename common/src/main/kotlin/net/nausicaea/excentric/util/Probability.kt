package net.nausicaea.excentric.util

import kotlin.math.expm1

/**
 * Denotes a real value that is bounded to the closed interval between 0 and 1.
 */
@ConsistentCopyVisibility
data class Probability private constructor(
    val value: Double,
) {
    /**
     * Addition operator for [Probability].
     */
    operator fun plus(rhs: Probability): Probability = of(value + rhs.value)

    /**
     * Multiplication operator for [Probability].
     */
    operator fun times(rhs: Probability) = of(value * rhs.value)

    /**
     * Calculate the inverse probability (i.e. `1 - p`).
     */
    fun inv() = of(1.0 - value)

    /**
     * Return `true` if the [Probability] value is approximately zero.
     */
    val isApproxZero: Boolean get() = value < java.lang.Double.MIN_NORMAL

    /**
     * Return `true` if the [Probability] is meaningfully greater than zero.
     */
    val isNonZero: Boolean get() = !isApproxZero

    companion object {
        /**
         * Static zero probability.
         */
        @JvmField
        val ZERO = Probability(0.0)

        /**
         * Static 100% probability.
         */
        @JvmField
        val ONE = Probability(1.0)

        /**
         * Construct a new probability value, clamping the [Double] to the closed interval `[0, 1]`.
         */
        fun of(value: Double) = Probability(value.coerceIn(0.0, 1.0))

        /**
         * Calculate the arithmetic mean value of a collection of probabilities.
         */
        fun mean(src: Collection<Probability>): Probability =
            if (src.isEmpty()) {
                ZERO
            } else {
                of(src.sumOf { it.value } / src.size)
            }

        /**
         * Calculate a simplified form of the Boltzmann equation given an input probability and temperature.
         *
         * ```
         * exp(x / t) - 1
         * ```
         */
        fun boltzmann(
            x: Probability,
            temperature: Probability,
        ) = of(expm1(x.value / temperature.value))
    }
}
