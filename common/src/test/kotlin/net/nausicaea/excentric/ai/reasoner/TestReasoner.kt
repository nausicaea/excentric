package net.nausicaea.excentric.ai.reasoner

import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.Weighted
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TestReasoner {
    private val a = ExampleAction("a")
    private val b = ExampleAction("b")

    @Test
    fun planSelectsTheOnlyCandidateIfTheWeightIsNonZero() {
        val r =
            ExampleReasoner(
                scores = mapOf(a to Probability.of(0.5), b to Probability.of(0.6)),
                action = null,
            )
        r.plan({ sequenceOf(a) }, { 0.0 })
        assertEquals(a, r.action?.item)
    }

    @Test
    fun planSelectsNothingIfTheOnlyCandidateHasZeroWeight() {
        val r =
            ExampleReasoner(
                scores = mapOf(a to Probability.of(0.0), b to Probability.of(0.6)),
                action = null,
            )
        r.plan({ sequenceOf(a) }, { 0.0 })
        assertNull(r.action)
    }
}

private class ExampleReasoner(
    private val hysteresis: Double = 0.2,
    private val scores: Map<ExampleAction, Probability> = emptyMap(),
    var action: Weighted<ExampleAction>? = null,
) : Reasoner<ExampleAction, ReasonerContext<ExampleAction>> {
    val scoredActions = mutableListOf<ExampleAction>()

    override fun hysteresis(): Double = hysteresis

    override fun currentActionWeight(): Probability? = this.action?.weight

    override fun setCurrentAction(action: Weighted<ExampleAction>) {
        this.action = action
    }

    override fun score(
        actions: Sequence<ExampleAction>,
        context: ReasonerContext<ExampleAction>,
    ): Sequence<Weighted<ExampleAction>> =
        actions
            .onEach { scoredActions += it }
            .map { Weighted(it, scores[it]!!) }

    override fun select(
        scoredActions: Sequence<Weighted<ExampleAction>>,
        rng: ReasonerRng,
    ): Weighted<ExampleAction>? = scoredActions.maxByOrNull { it.weight.value }
}

private data class ExampleAction(
    val name: String,
) : Action {
    override fun apply(): Future<Try<Probability>> {
        TODO("Not part of the test")
    }
}
