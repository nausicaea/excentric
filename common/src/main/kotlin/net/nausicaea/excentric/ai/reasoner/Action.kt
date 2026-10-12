package net.nausicaea.excentric.ai.reasoner

import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try

interface Action {
    /**
     * Apply the action, returning a promised deferred result.
     */
    fun apply(): Future<Try<Probability>>
}
