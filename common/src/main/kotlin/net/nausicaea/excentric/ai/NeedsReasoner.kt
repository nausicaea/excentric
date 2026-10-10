package net.nausicaea.excentric.ai

import net.minecraft.resources.ResourceKey
import net.minecraft.util.RandomSource
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.ai.prerequisite.PrerequisiteState
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.minecraft.util.chooseWeighted
import net.nausicaea.excentric.util.None
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.boltzmann
import net.nausicaea.excentric.util.Some
import net.nausicaea.excentric.util.Weighted
import net.nausicaea.excentric.util.andThen
import net.nausicaea.excentric.util.forEach
import net.nausicaea.excentric.util.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Stream

class NeedsReasoner : Reasoner<Service, NeedsContext> {
    private var currentService: Option<SvcPrq> = None

    override fun setAction(action: Service) {
        this.currentService = Some(SvcPrq(action))
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
    ): Option<Service> = rng.chooseWeighted(scoredActions.toList())

    /**
     * If a [Service] is currently selected as a target, try to
     * [Service.realise] the [Service] against the corresponding [Need]. In other
     * words, try to satisfy the matching [Need] with the current [Service]. This
     * guarantees to call [Prerequisite.poll] exactly once. Call
     * [Service.realise] only if the prerequisite is satisfied (e.g. the return
     * value from [Prerequisite.poll] is equal to [PrerequisiteState.SATISFIED]).
     */
    fun tryRealiseService(context: NeedsContext) {
        currentService
            .map(SvcPrq::poll)
            .andThen(SvcPolled::tryRealise)
            .forEach({ t -> context.needs().realise(t.key, t.addedAmount) })
    }
}

/**
 * Denotes a service with an unfulfilled prerequisite.
 */
private data class SvcPrq(
    val svc: Service,
    val prq: Prerequisite,
) {
    constructor(svc: Service) : this(svc, svc.prerequisite())

    /**
     * Poll the [Prerequisite] once.
     */
    fun poll(): SvcPolled = SvcPolled(svc, prq.poll())
}

/**
 * Denotes a service whose prerequisite was just polled.
 */
private data class SvcPolled(
    val svc: Service,
    val state: PrerequisiteState,
) {
    /**
     * Evaluate [PrerequisiteState] and call [Service.realise] only if `state`
     * matches [PrerequisiteState.SATISFIED].
     */
    fun tryRealise(): Option<SvcRealised> {
        val svcKey: ResourceKey<Need> = svc.key()
        return when (state) {
            PrerequisiteState.PENDING -> {
                None
            }

            PrerequisiteState.FAILED -> {
                LOG.warn(LOG_MARKER, "Prerequisite of service has state failed")
                Some(SvcRealised(svcKey, Probability.ZERO))
            }

            PrerequisiteState.SATISFIED -> {
                Some(SvcRealised(svcKey, svc.realise()))
            }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(NeedsReasoner::class.java)
    }
}

/**
 * Denotes a realised service.
 */
private data class SvcRealised(
    val key: ResourceKey<Need>,
    val addedAmount: Probability,
)
