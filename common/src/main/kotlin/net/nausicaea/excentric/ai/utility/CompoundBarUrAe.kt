package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Probability

/**
 * The utility is the product of the [BaseUtility], [Urgency], and [AerialDistance].
 */
object CompoundBarUrAe : UtilityFn {
    override fun score(
        svc: Service,
        prq: Prerequisite,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability = BaseUtility.score(svc, prq, sat, ctx) * Urgency.score(svc, prq, sat, ctx) * AerialDistance.score(svc, prq, sat, ctx)
}
