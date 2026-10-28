package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.ready
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

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
    override fun fulfil(entity: LivingEntity): Future<Try<Unit>> =
        if (entity is Mob) {
            PositionFuture(entity.navigation, entity, pos, pathfindingTolerance, speedModifier)
        } else {
            ready(Err(UnsupportedOperationException("pathfinding is only supported for subclasses of Mob")))
        }
}

class PathfindingImpossible : Throwable("cannot establish a pathfinding route")

class PathfindingStuck : Throwable("pathfinding has timed out and is stuck")

class PositionFuture(
    val nav: PathNavigation,
    val entity: Mob,
    val pos: Vec3,
    val pathfindingTolerance: Int,
    val speedModifier: Double,
) : Future<Try<Unit>> {
    var initialised: Boolean = false

    override fun poll(): Poll<Try<Unit>> {
        if (!initialised) {
            initialised = true
            val path =
                nav.createPath(pos.x, pos.y, pos.z, pathfindingTolerance)
                    ?: return Ready(Err(PathfindingImpossible()))
            nav.moveTo(path, speedModifier)
        }

        if (nav.isStuck) {
            return Ready(Err(PathfindingStuck()))
        }
        if (nav.isDone) {
            return Ready(Ok(Unit))
        }
        return Pending
    }

    override fun close() {
        nav.stop()
    }
}
