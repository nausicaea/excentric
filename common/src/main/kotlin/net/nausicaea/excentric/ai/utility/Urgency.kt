package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Probability

/**
 * Calculate the utility as the inverse probability of the need's satisfaction.
 */
object Urgency : UtilityFn {
    override fun score(
        svc: Service,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability = sat.inv()
}
