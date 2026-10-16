package net.nausicaea.excentric.ai.need

import net.nausicaea.excentric.ai.decay.DecayFn
import net.nausicaea.excentric.ai.utility.UtilityFn

interface Need {
    fun decayFn(): DecayFn

    fun utilityFn(): UtilityFn
}
