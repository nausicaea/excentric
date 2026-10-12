package net.nausicaea.excentric.ai

import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.minecraft.util.chooseWeighted
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.boltzmann
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.andThen
import net.nausicaea.excentric.util.forEach
import net.nausicaea.excentric.util.map
import net.nausicaea.excentric.util.ok
import net.nausicaea.excentric.util.ready
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Stream


class NeedsReasoner : Reasoner<Service, NeedsContext> {
    private var currentService: SvcFut? = null

    override fun setAction(action: Service) {
        this.currentService = SvcFut(action.key(), action.use())
    }

    // / For every service, call [NeedsCollection#score(Service, NeedsContext)]
    override fun score(
        actions: Stream<Service>,
        context: NeedsContext,
    ): Stream<Weighted<Service>> {
        val needs = context.needs()
        val temp = needs.urgency()
        return actions
            .map { svc ->
                Weighted(
                    svc,
                    boltzmann(needs.score(svc, context), temp),
                )
            }.filter { svc -> svc.weight.isNonZero }
    }

    // / Randomly select from the available [Service]s by their score.
    override fun select(
        scoredActions: Stream<Weighted<Service>>,
        rng: RandomSource,
    ): Service? = rng.chooseWeighted(scoredActions.toList())

    /**
     * If a [Service] is currently selected as a target, try to
     * [Service.use] the [Service] against the corresponding [Need]. In other
     * words, try to satisfy the matching [Need] with the current [Service].
     */
    fun tryRealiseService(context: NeedsContext) {
        currentService
            .map { Pair(it.key, it.fut.poll()) }
            .andThen { it.second.ready().map { iit -> Pair(it.first, iit) } }
            .andThen { it.second.ok().map { iit -> Pair(it.first, iit) } }
            .forEach { context.needs().realise(it.first, it.second) }
    }
}

/**
 * Denotes an unused service.
 */
private data class SvcFut(
    val key: ResourceKey<Need>,
    val fut: Future<Try<Probability>>,
)
