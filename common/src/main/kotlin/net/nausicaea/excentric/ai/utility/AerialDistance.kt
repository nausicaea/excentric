package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.of
import kotlin.math.abs
import kotlin.math.max

/**
 * Calculate the utility as the multiplicative inverse (`1/x`) of the
 * [net.minecraft.world.entity.Entity] distance to the service (as the
 * crow flies).
 */
object AerialDistance : UtilityFn {
    override fun score(
        svc: Service,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability {
        val prq = svc.prerequisite()
        if (prq.isNone()) {
            return Probability.ONE
        }
        return when (val prq = prq.unwrap()) {
            is Position -> {
                of(
                    1.0 /
                            max(
                                1.0,
                                abs(ctx.entity().position().distanceTo(prq.pos)),
                            ),
                )
            }
            else -> {
                Probability.ONE
            }
        }
    }
}
