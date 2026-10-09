package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.java.util.Probability

/**
 * Calculate the utility as [Service#baseUtility()].
 */
object BaseUtility : UtilityFn {
    override fun score(
        svc: Service,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability = svc.baseUtility()
}
