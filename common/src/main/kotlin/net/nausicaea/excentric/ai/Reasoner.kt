package net.nausicaea.excentric.ai

import net.minecraft.util.RandomSource
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.forEach
import java.util.Optional
import java.util.stream.Stream

/**
 * Describes a utility-based AI reasoning system.
 */
interface Reasoner<A, C : ReasonerContext<A>> {
    /**
     * Set the currently active action.
     */
    fun setAction(action: A)

    /**
     * Score actions relative to a context.
     */
    fun score(
        actions: Stream<A>,
        context: C,
    ): Stream<Weighted<A>>

    /**
     * Based on the action weights, select at most one next action.
     */
    fun select(
        scoredActions: Stream<Weighted<A>>,
        rng: RandomSource,
    ): Option<A>

    /**
     * Plan the next action for the [Reasoner]:
     * 1. Collect potential actions
     * 2. Score each action
     * 3. Select the next action
     */
    fun plan(
        context: C,
        rng: RandomSource,
    ) {
        select(score(context.collect(), context), rng).forEach(this::setAction)
    }
}
