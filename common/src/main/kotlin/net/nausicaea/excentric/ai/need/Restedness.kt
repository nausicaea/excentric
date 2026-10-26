package net.nausicaea.excentric.ai.need

import net.nausicaea.excentric.ai.decay.DecayFn
import net.nausicaea.excentric.ai.decay.Linear
import net.nausicaea.excentric.ai.utility.AerialDistance
import net.nausicaea.excentric.ai.utility.BaseUtility
import net.nausicaea.excentric.ai.utility.NotSleeping
import net.nausicaea.excentric.ai.utility.Urgency
import net.nausicaea.excentric.ai.utility.UtilityFn

/**
 * Represents the need to be rested, i.e. to sleep.
 *
 * TODO: balance decay rate and utility function. Currently placeholders.
 */
object Restedness : Need {
    private val d: DecayFn = Linear(0.0001)
    private val u: UtilityFn = NotSleeping * BaseUtility * Urgency * AerialDistance

    override fun decayFn(): DecayFn = d

    override fun utilityFn(): UtilityFn = u
}
