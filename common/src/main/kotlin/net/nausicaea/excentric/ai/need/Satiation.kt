package net.nausicaea.excentric.ai.need

import net.nausicaea.excentric.ai.decay.DecayFn
import net.nausicaea.excentric.ai.decay.Linear
import net.nausicaea.excentric.ai.utility.CompoundBarUrAe
import net.nausicaea.excentric.ai.utility.UtilityFn

data class Satiation(
    val d: DecayFn,
    val u: UtilityFn,
) : Need {
    constructor() : this(Linear(1.0), CompoundBarUrAe)

    override fun decayFn(): DecayFn = d

    override fun utilityFn(): UtilityFn = u
}
