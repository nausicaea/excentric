package net.nausicaea.excentric.ai.reasoner

interface ReasonerContext<A> {
    /**
     * Collect services from various sources.
     */
    fun collect(): Sequence<A>
}
