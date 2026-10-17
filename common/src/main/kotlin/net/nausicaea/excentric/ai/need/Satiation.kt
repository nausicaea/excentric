package net.nausicaea.excentric.ai.need

import net.nausicaea.excentric.ai.decay.DecayFn
import net.nausicaea.excentric.ai.decay.Linear
import net.nausicaea.excentric.ai.utility.CompoundBarUrAe
import net.nausicaea.excentric.ai.utility.UtilityFn

/**
 * Represents the need to eat.
 *
 * TODO: balance decay rate and utility function. Currently placeholders.
 */
object Satiation : Need {
    private val d: DecayFn = Linear(0.0001)
    private val u: UtilityFn = CompoundBarUrAe

    override fun decayFn(): DecayFn = d

    override fun utilityFn(): UtilityFn = u
}
