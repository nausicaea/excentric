package net.nausicaea.excentric.ai

import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.debug.ServiceDebug
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.reasoner.Reasoner
import net.nausicaea.excentric.ai.reasoner.ReasonerRng
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.boltzmann
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.asOpt
import net.nausicaea.excentric.util.inspectErr
import net.nausicaea.excentric.util.ok
import net.nausicaea.excentric.util.ready
import net.nausicaea.excentric.util.unwrapOr
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NeedsReasoner : Reasoner<Service, NeedsContext> {
    private var currentService: SvcFut? = null
    private val hysteresis: Double = 0.2

    override fun threshold(): Probability =
        currentService
            .asOpt()
            .map { it.weight + hysteresis }
            .unwrapOr(Probability.ZERO)

    override fun setCurrentAction(action: Weighted<Service>) {
        currentService = SvcFut(action.weight, action.item, action.item.key(), action.item.apply())
    }

    /**
     * For every service, call [NeedsCollection.score].
     */
    override fun score(
        actions: Sequence<Service>,
        context: NeedsContext,
    ): Sequence<Weighted<Service>> {
        val needs = context.needs()
        val temp = needs.urgency()
        if (temp.isApproxZero) {
            return emptySequence()
        }
        return actions
            .map {
                Weighted(
                    it,
                    boltzmann(needs.score(it, context), temp),
                )
            }.filter { it.weight.isNonZero }
    }

    /**
     * Randomly select from the available [Service]s by their score.
     *
     * It is intended behaviour that the currently active service [currentService] can be replaced by a new, more urgent need.
     */
    override fun select(
        scoredActions: Sequence<Weighted<Service>>,
        rng: ReasonerRng,
    ): Weighted<Service>? = rng.chooseWeighted(scoredActions.toList())

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
            .inspect {
                currentService = null
                it.state.inspectErr { iit -> LOG.info(LOG_MARKER, "Error using service for need ${it.key}", iit) }
            }.andThen { it.state.ok().map { iit -> SvcUsed(it.key, iit) } }
            .forEach { context.needs().realise(it.key, it.benefit) }
    }

    /**
     * Describe the currently active [Service] for debug displays, or return `null` if there is none.
     */
    fun debugCurrentService(context: NeedsContext): ServiceDebug? =
        currentService?.let {
            ServiceDebug(
                need = it.key.location(),
                type = it.svc::class.simpleName ?: "unknown",
                description = it.svc.describe(),
                baseUtility = it.svc.baseUtility().value,
                score = context.needs().score(it.svc, context).value,
                weight = it.weight.value,
            )
        }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(NeedsReasoner::class.java)
    }
}

/**
 * Denotes a service that is currently being used and waiting on completion.
 */
private data class SvcFut(
    val weight: Probability,
    val svc: Service,
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
