package net.nausicaea.excentric.ai.reasoner

import net.minecraft.util.RandomSource
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.asOpt
import net.nausicaea.excentric.util.toSeq

/**
 * Describes a utility-based AI reasoning system.
 */
interface Reasoner<A : Action, C : ReasonerContext<A>> {
    /**
     * Get the currently active action.
     */
    fun currentAction(): A?

    /**
     * Return `true` if the specified action matches the current one.
     */
    fun isCurrentAction(action: A): Boolean

    /**
     * Set the currently active action.
     */
    fun setCurrentAction(action: A)

    /**
     * Score actions relative to a context.
     */
    fun score(
        actions: Sequence<A>,
        context: C,
    ): Sequence<Weighted<A>>

    /**
     * Based on the action weights, select at most one next action.
     */
    fun select(
        scoredActions: Sequence<Weighted<A>>,
        rng: RandomSource,
    ): A?

    /**
     * Plan the next action for the [Reasoner]:
     * 1. Collect potential actions
     * 2. Prepend the current action to the set of actions
     * 3. Score each action
     * 4. Select the next action
     */
    fun plan(
        context: C,
        rng: RandomSource,
    ) {
        val actions = toSeq(currentAction()) + context.collect()
        val scored = score(actions, context)
        select(scored, rng).asOpt().forEach(this::setCurrentAction)
    }
}
