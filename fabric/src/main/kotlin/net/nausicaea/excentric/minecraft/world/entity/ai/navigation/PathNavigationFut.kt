package net.nausicaea.excentric.minecraft.world.entity.ai.navigation

import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.level.pathfinder.Path
import net.nausicaea.excentric.ai.navigation.PathfindingImpossible
import net.nausicaea.excentric.ai.navigation.PathfindingStuck
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.util.future.FusedFuture
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready

class PathNavigationFut(
    val prq: Position,
    private var nav: PathNavigation?,
) : FusedFuture<@JvmSuppressWildcards Try<Unit>> {
    private var path: Path? = null

    override fun isTerminated(): Boolean = nav == null

    @Suppress("ReturnCount")
    override fun poll(): Poll<Try<Unit>> {
        if (nav == null) {
            return Pending
        }

        if (path == null) {
            path = nav?.createPath(prq.pos.x, prq.pos.y, prq.pos.z, prq.pathfindingTolerance)
            if (path == null) {
                close()
                return Ready(Err(PathfindingImpossible()))
            }
            nav?.moveTo(path, prq.speedModifier)
        }

        if (nav?.isStuck ?: false) {
            close()
            return Ready(Err(PathfindingStuck()))
        }
        if (nav?.isDone ?: false) {
            close()
            return Ready(Ok(Unit))
        }
        return Pending
    }

    override fun close() {
        if (path != null && nav?.path == path) {
            nav?.stop()
        }
        nav = null
    }
}
