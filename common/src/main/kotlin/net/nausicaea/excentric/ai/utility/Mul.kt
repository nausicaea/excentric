package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Probability

class Mul(
    val a: UtilityFn,
    val b: UtilityFn,
) : UtilityFn {
    override fun score(
        svc: Service,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability = a.score(svc, sat, ctx) * b.score(svc, sat, ctx)
}
