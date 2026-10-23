package net.nausicaea.excentric.ai.debug

/**
 * Implemented (through a mixin) by entities that own a [net.nausicaea.excentric.ai.NeedsCollection] and a
 * [net.nausicaea.excentric.ai.NeedsReasoner], so that debug displays can inspect them.
 */
interface NeedsDebugSource {
    /**
     * Capture the current needs and active service of this entity.
     */
    fun villagerModDebugSnapshot(): VillagerDebug
}
