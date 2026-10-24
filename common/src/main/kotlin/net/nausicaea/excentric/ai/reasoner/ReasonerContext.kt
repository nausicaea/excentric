package net.nausicaea.excentric.ai.reasoner

fun interface ReasonerContext<A> {
    /**
     * Collect services from various sources.
     */
    fun collect(): Sequence<A>
}
