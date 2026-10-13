package net.nausicaea.excentric.ai

import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.decay.DecayFn
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.ai.utility.UtilityFn
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.toOpt
import net.nausicaea.excentric.util.unwrapOr
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.math.max

/**
 * A mutable set of [Need]s that decay over time (see
 * [NeedsCollection.decay]), can be satisfied by [Service]s (see
 * [NeedsCollection.realise]), and can
 * score [Service]s.
 */
class NeedsCollection {
    val index: HashMap<ResourceKey<Need>, Int> = HashMap()
    val decayFns: HashMap<Int, DecayFn> = HashMap()
    val utilityFns: HashMap<Int, UtilityFn> = HashMap()
    val satisfactions: HashMap<Int, Probability> = HashMap()
    private var maxIndex = 0
    private var lastUpdateTime = 0L

    /**
     * Return `true` if the collection contains at least one Need.
     */
    val isEmpty: Boolean
        get() = index.isEmpty()

    /**
     * Return the number of [Need]s in the collection.
     */
    val size: Int
        get() = index.size

    /**
     * Add a [Need] to the collection. Return `true` if a need was added, `false`
     * if this need was already present.
     */
    fun add(
        need: Need,
        initialSatisfaction: Probability,
    ): Boolean =
        NEED_REGISTRY
            .getResourceKey(need)
            .toOpt()
            .filter { !this.index.containsKey(it) }
            .map {
                index[it] = maxIndex
                decayFns[maxIndex] = need.decayFn()
                utilityFns[maxIndex] = need.utilityFn()
                satisfactions[maxIndex] = initialSatisfaction
                maxIndex += 1
                true
            }.unwrapOr(false)

    /**
     * Remove a particular [Need] by its resource key. Return `true` if a need was
     * removed, `false` otherwise.
     */
    fun remove(needKey: ResourceKey<Need>): Boolean {
        if (!this.index.containsKey(needKey)) {
            return false
        }
        val idx = this.index[needKey]
        this.decayFns.remove(idx)
        this.utilityFns.remove(idx)
        this.satisfactions.remove(idx)
        this.index.remove(needKey)
        return true
    }

    /**
     * Delete all [Need]s.
     */
    fun clear() {
        this.index.clear()
        this.decayFns.clear()
        this.utilityFns.clear()
        this.satisfactions.clear()
    }

    /**
     * Slowly reduce the [Need]'s satisfaction level through [Need.decayFn].
     */
    fun decay(monotonicTime: Long) {
        val deltaTime = max(0L, monotonicTime - lastUpdateTime).toDouble()
        lastUpdateTime = monotonicTime
        for (i in index.values) {
            satisfactions.compute(
                i,
            ) { k, v ->
                if (v == null) {
                    // Assume unknown needs have a satisfaction of one.
                    return@compute Probability.ONE
                } else {
                    return@compute decayFns[k]!!.decay(v, deltaTime)
                }
            }
        }

        if (this.isCritical) {
            LOG.info(LOG_MARKER, "At least one need is in critical condition")
        }
    }

    val isCritical: Boolean
        get() = satisfactions.values.any(Probability::isSubnormal)

    fun urgency(): Probability = Probability.mean(satisfactions.values).inv()

    /**
     * If a [Need] matching the [Service] can be found, calculate a score based on
     * the need's [UtilityFn]. Otherwise, return `0`. The score is based on the
     * [Service]'s data, the corresponding [Need]'s initialSatisfaction level, the
     * overall urgency (i.e. `1 - mean(initialSatisfaction)`), and any
     * external context.
     */
    fun score(
        svc: Service,
        context: NeedsContext,
    ): Probability {
        val i = index[svc.key()] ?: return Probability.ZERO
        return utilityFns[i]!!.score(svc, satisfactions[i]!!, context)
    }

    fun realise(
        key: ResourceKey<Need>,
        addedIntensity: Probability,
    ) {
        index[key]?.let { i ->
            // Assume unknown needs are filled up by
            // `addedIntensity`, starting at zero.
            satisfactions.compute(
                i,
            ) { _, v -> v?.plus(addedIntensity) ?: addedIntensity }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(NeedsCollection::class.java)
    }
}
