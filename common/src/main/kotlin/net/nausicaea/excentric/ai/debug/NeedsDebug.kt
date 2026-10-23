package net.nausicaea.excentric.ai.debug

import net.nausicaea.excentric.ai.NeedsContext
import net.nausicaea.excentric.ai.NeedsReasoner

/**
 * Builds [VillagerDebug] snapshots from the needs and the reasoner of an entity.
 */
object NeedsDebug {
    @JvmStatic
    fun snapshot(
        entityId: Int,
        ctx: NeedsContext,
        reasoner: NeedsReasoner,
    ): VillagerDebug {
        val needs = ctx.needs()
        val entries =
            needs.index.entries
                .sortedBy { it.key.location().toString() }
                .map { (key, i) -> NeedDebug(key.location(), needs.satisfactions[i]?.value ?: 0.0) }
        return VillagerDebug(entityId, entries, reasoner.debugCurrentService(ctx))
    }
}
