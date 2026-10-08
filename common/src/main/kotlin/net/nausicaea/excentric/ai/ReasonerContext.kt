package net.nausicaea.excentric.ai

import java.util.stream.Stream

interface ReasonerContext<A> {
    /**
     * Collect services from various sources.
     */
    fun collect(): Stream<A>
}
