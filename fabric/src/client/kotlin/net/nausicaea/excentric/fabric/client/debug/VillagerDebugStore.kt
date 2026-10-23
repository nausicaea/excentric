package net.nausicaea.excentric.fabric.client.debug

import net.nausicaea.excentric.ai.debug.VillagerDebug

/**
 * Holds the most recent [VillagerDebug] snapshots received from the server.
 *
 * The server sends a complete set of snapshots every few ticks. If nothing has been received for [MAX_AGE_TICKS]
 * (for example because the server stopped sending), the display is cleared.
 */
object VillagerDebugStore {
    private const val MAX_AGE_TICKS = 40L
    private var entries: List<VillagerDebug> = emptyList()
    private var now = 0L
    private var lastUpdate = 0L

    fun update(newEntries: List<VillagerDebug>) {
        entries = newEntries
        lastUpdate = now
    }

    fun clear() {
        entries = emptyList()
    }

    fun tick() {
        now += 1
        if (now - lastUpdate > MAX_AGE_TICKS) {
            clear()
        }
    }

    fun current(): List<VillagerDebug> = entries
}
