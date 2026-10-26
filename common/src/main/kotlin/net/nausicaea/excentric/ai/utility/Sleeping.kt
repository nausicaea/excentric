package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Probability

object Sleeping : UtilityFn {
    override fun score(
        svc: Service,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability =
        if (ctx.entity().isSleeping) {
            Probability.ONE
        } else {
            Probability.ZERO
        }
}
