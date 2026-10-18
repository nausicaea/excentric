package net.nausicaea.excentric.ai.reasoner

import net.minecraft.util.RandomSource
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.asOpt
import net.nausicaea.excentric.util.toSeq

/**
 * Describes a utility-based AI reasoning system.
 */
interface Reasoner<A : Action, C : ReasonerContext<A>> {
    /**
     * Specify the size of the hysteresis necessary for a selected service to override the currently active service.
     */
    fun hysteresis(): Double

    /**
     * Get the currently active action's weight.
     */
    fun currentActionWeight(): Probability?

    /**
     * Set the currently active action.
     */
    fun setCurrentAction(action: Weighted<A>)

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
    ): Weighted<A>?

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
        val actions = context.collect()
        val scored = score(actions, context)
        val selected = select(scored, rng)

        selected
            .asOpt()
            .filter { it.weight > (currentActionWeight() ?: Probability.ZERO) + hysteresis() }
            .forEach(this::setCurrentAction)
    }
}
