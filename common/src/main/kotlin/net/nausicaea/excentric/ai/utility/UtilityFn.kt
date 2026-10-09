package net.nausicaea.excentric.ai.utility

import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Probability

/**
 * Resource curves convert a [Service#baseUtility()] to a subjective utility.
 */
interface UtilityFn {
    /**
     * Score a [Service] by the existing
     * [net.nausicaea.excentric.minecraft.world.entity.ai.need.Need] initialSatisfaction
     * value, the current temperature, and the external context.
     *
     * The return value is guaranteed to be normalised to [0..1].
     */
    fun score(
        svc: Service,
        sat: Probability,
        ctx: UtilityFnContext,
    ): Probability
}
