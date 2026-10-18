package net.nausicaea.excentric.ai.reasoner

import net.minecraft.util.RandomSource
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.Weighted
import kotlin.test.Test
import kotlin.test.assertEquals

const val SEED: Long = 1234L

class TestReasoner {
    private val a = ExampleAction("a")
    private val b = ExampleAction("b")

    @Test
    fun planSelectsTheOnlyCandidateIfTheWeightIsNonZero() {
        val r =
            ExampleReasoner(
                scores = mapOf(a to Probability.of(0.5), b to Probability.of(0.6)),
                action = Weighted(a, Probability.of(0.5)),
            )
        r.plan({ sequenceOf(a) }, RandomSource.create(SEED))
        assertEquals(a, r.action?.item)
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
        rng: RandomSource,
    ): Weighted<ExampleAction>? = scoredActions.maxByOrNull { it.weight.value }
}

private data class ExampleAction(
    val name: String,
) : Action {
    override fun apply(): Future<Try<Probability>> {
        TODO("Not part of the test")
    }
}
