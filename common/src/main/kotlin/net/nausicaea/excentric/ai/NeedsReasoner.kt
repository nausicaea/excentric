package net.nausicaea.excentric.ai

import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.reasoner.Reasoner
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.minecraft.util.chooseWeighted
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.boltzmann
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.asOpt
import net.nausicaea.excentric.util.ok
import net.nausicaea.excentric.util.ready
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NeedsReasoner : Reasoner<Service, NeedsContext> {
    private var currentService: SvcFut? = null

    override fun setAction(action: Service) {
        this.currentService = SvcFut(action.key(), action.apply())
    }

    // / For every service, call [NeedsCollection#score(Service, NeedsContext)]
    override fun score(
        actions: Sequence<Service>,
        context: NeedsContext,
    ): Sequence<Weighted<Service>> {
        val needs = context.needs()
        val temp = needs.urgency()
        return actions
            .map {
                val w =
                    Weighted(
                        it,
                        boltzmann(needs.score(it, context), temp),
                    )
                LOG.debug(LOG_MARKER, "Weight {}", w.weight)
                w
            }.filter { it.weight.isNonZero }
    }

    // / Randomly select from the available [Service]s by their score.
    override fun select(
        scoredActions: Sequence<Weighted<Service>>,
        rng: RandomSource,
    ): Service? = rng.chooseWeighted(scoredActions.toList())

    /**
     * If a [Service] is currently selected as a target, try to
     * [Service.apply] the [Service] against the corresponding [Need]. In other
     * words, try to satisfy the matching [Need] with the current [Service].
     */
    fun tryRealiseService(context: NeedsContext) {
        currentService
            .asOpt()
            .map { SvcFutPolled(it.key, it.fut.poll()) }
            .andThen { it.state.ready().map { iit -> SvcFutReady(it.key, iit) } }
            .andThen { it.state.ok().map { iit -> SvcUsed(it.key, iit) } }
            .forEach {
                context.needs().realise(it.key, it.benefit)
                currentService = null
            }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NeedsReasoner::class.java)
    }
}

/**
 * Denotes a service that is currently being used and waiting on completion.
 */
private data class SvcFut(
    val key: ResourceKey<Need>,
    val fut: Future<Try<Probability>>,
)

/**
 * Denotes a service-related future that was just polled.
 */
private data class SvcFutPolled(
    val key: ResourceKey<Need>,
    val state: Poll<Try<Probability>>,
)

/**
 * The future is ready, meaning service usage has completed, but we don't yet know if it was successful.
 */
private data class SvcFutReady(
    val key: ResourceKey<Need>,
    val state: Try<Probability>,
)

/**
 * The service has been used successfully, and we have the actual benefit for the consumer.
 */
private data class SvcUsed(
    val key: ResourceKey<Need>,
    val benefit: Probability,
)
