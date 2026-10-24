package net.nausicaea.excentric.ai

import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.decay.DecayFn
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.ai.utility.UtilityFn
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.asOpt
import net.nausicaea.excentric.util.toOpt
import net.nausicaea.excentric.util.unwrapOr
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max

/**
 * A mutable set of [Need]s that decay over time (see
 * [NeedsCollection.decay]), can be satisfied by [Service]s (see
 * [NeedsCollection.realise]), and can
 * score [Service]s.
 */
class NeedsCollection {
    val index: MutableMap<ResourceKey<Need>, Int> = mutableMapOf()
    val satisfactions: MutableMap<Int, Probability> = mutableMapOf()
    val decayFns: MutableMap<Int, DecayFn> = mutableMapOf()
    val utilityFns: MutableMap<Int, UtilityFn> = mutableMapOf()
    private var maxIndex = 0
    private var lastUpdateTime: Long? = null

    fun addReplaceMap(sat: Map<ResourceLocation, Probability>) {
        for ((rLoc, prob) in sat) {
            val key = ResourceKey.create(NEED_REGISTRY.key(), rLoc)
            if (index.containsKey(key)) {
                val i = index[key]!!
                satisfactions[i] = prob
            } else {
                val need = NEED_REGISTRY.getOptional(key).getOrNull() ?: continue
                index[key] = maxIndex
                satisfactions[maxIndex] = prob
                decayFns[maxIndex] = need.decayFn()
                utilityFns[maxIndex] = need.utilityFn()
                maxIndex += 1
            }
        }
    }

    fun toMap(): Map<ResourceLocation, Probability> = index.entries.associate { (key, i) -> key.location() to satisfactions[i]!! }

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
            .filter { !index.containsKey(it) }
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
        val idx = index[needKey] ?: return false
        decayFns.remove(idx)
        utilityFns.remove(idx)
        satisfactions.remove(idx)
        index.remove(needKey)
        return true
    }

    /**
     * Delete all [Need]s.
     */
    fun clear() {
        index.clear()
        decayFns.clear()
        utilityFns.clear()
        satisfactions.clear()
    }

    /**
     * Slowly reduce the [Need]'s satisfaction level through [Need.decayFn].
     */
    fun decay(monotonicTime: Long) {
        val deltaTime: Double =
            lastUpdateTime
                .asOpt()
                .map { max(0L, monotonicTime - it).toDouble() }
                .unwrapOr(0.0)
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
            // TODO: what do we do when the need is critical?
            LOG.info(LOG_MARKER, "Critical needs")
        }
    }

    val isCritical: Boolean
        get() = satisfactions.values.any(Probability::isApproxZero)

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
    ): Probability =
        index[svc.key()]
            .asOpt()
            .map { utilityFns[it]!!.score(svc, satisfactions[it]!!, context) }
            .unwrapOr(Probability.ZERO)

    fun realise(
        key: ResourceKey<Need>,
        benefit: Probability,
    ) {
        index[key]?.let { i ->
            // Assume unknown needs are filled up by
            // `addedIntensity`, starting at zero.
            satisfactions.compute(
                i,
            ) { _, v -> v?.plus(benefit) ?: benefit }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(NeedsCollection::class.java)
    }
}
