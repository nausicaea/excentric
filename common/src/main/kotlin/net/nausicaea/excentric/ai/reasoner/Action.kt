package net.nausicaea.excentric.ai.reasoner

import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Try

fun interface Action {
    /**
     * Apply the action, returning a promised deferred result.
     */
    fun apply(): Future<Try<Probability>>
}
