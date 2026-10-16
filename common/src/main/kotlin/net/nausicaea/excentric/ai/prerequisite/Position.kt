package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Try

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
     * Error term for position comparisons. Determines the maximum deviation from the target position that is acceptable for pathfinding.
     */
    val error: Int = 1,
) : Prerequisite {
    /**
     * Bring the [LivingEntity] closer to the target position by using [net.minecraft.world.entity.ai.navigation.PathNavigation]. Return [Poll.Pending] while the entity is still finding its way and [Poll.Ready] when pathfinding has completed or aborted with an error.
     */
    fun poll(entity: LivingEntity): Poll<Try<Unit>> {
        // Is the entity at the prerequisite position?
        if (entity.position().closerThan(pos, error.toDouble())) {
            return Poll.Ready(Try.Ok(Unit))
        }

        // Does the entity support pathfinding?
        if (entity !is Mob) {
            return Poll.Ready(
                Try.Err(
                    UnsupportedOperationException("Cannot path-find for entities that don't inherit from Mob"),
                ),
            )
        }

        val nav = entity.navigation
        // Start the pathfinding
        if (!(nav.isInProgress || nav.isStuck)) {
            nav
                .createPath(pos.x, pos.y, pos.z, error)
                ?.let { nav.moveTo(it, 1.0) }
        }
        return Poll.Pending
    }
}
