package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.ai.navigation.Navigator
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.ready
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Try

/**
 * Denotes a [Prerequisite] position: the entity that wants to satisfy this
 * condition must navigate to the stored [Vec3].
 */
data class Position(
    /**
     * The target position that needs to be navigated to.
     */
    val pos: Vec3,
    /**
     * Error term for the pathfinding algorithm. Determines the accuracy necessary for pathfinding.
     */
    val pathfindingTolerance: Int = 1,
    /**
     * Error term for position comparisons. Determines the maximum deviation from the target position that is acceptable for pathfinding.
     */
    val arrivalTolerance: Double = 1.5,
    val speedModifier: Double = 0.5,
) : Prerequisite {
    override fun <T : Any> fulfil(subject: T): Future<Try<Unit>> =
        if (subject is Navigator) {
            subject.`villageMod$navigateTo`(this)
        } else {
            ready(
                Err(
                    UnsupportedOperationException(
                        "pathfinding is only supported for types that implement Navigator",
                    ),
                ),
            )
        }
}
