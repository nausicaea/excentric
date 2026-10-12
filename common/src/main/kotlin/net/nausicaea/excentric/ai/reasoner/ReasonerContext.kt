package net.nausicaea.excentric.ai.reasoner

import java.util.stream.Stream

interface ReasonerContext<A> {
    /**
     * Collect services from various sources.
     */
    fun collect(): Stream<A>
}
