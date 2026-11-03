package net.nausicaea.excentric.ai.navigation

import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.util.future.FusedFuture
import net.nausicaea.excentric.util.result.Try

interface Navigator {
    @Suppress("FunctionName")
    fun `villageMod$navigateTo`(pos: Position): FusedFuture<Try<Unit>>
}
