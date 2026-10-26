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
            needs
                .toMap()
                .entries
                .sortedBy { it.key.toString() }
                .map { (key, s) -> NeedDebug(key, s.value) }
        return VillagerDebug(entityId, entries, reasoner.debugCurrentService(ctx))
    }
}
