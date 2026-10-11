@file:Suppress("UnstableApiUsage")

package net.nausicaea.excentric.fabric.api.attachment

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.core.UUIDUtil
import net.minecraft.world.level.block.entity.BlockEntity
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.id
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.filter
import net.nausicaea.excentric.util.forEach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.ArrayDeque
import java.util.Queue
import java.util.UUID

val VILLAGE_REF: AttachmentType<UUID> = AttachmentRegistry.createPersistent(id("village_ref"), UUIDUtil.CODEC)

object VillageRef {
    private val LOG: Logger = LoggerFactory.getLogger(VillageRef::class.java)
    private val PENDING: Queue<PendingClaim> = ArrayDeque()

    fun get(target: AttachmentTarget): Option<UUID> = Option.of(target.getAttached(VILLAGE_REF))

    fun claim(
        target: AttachmentTarget,
        id: UUID,
    ): Boolean {
        if (target.hasAttached(VILLAGE_REF)) {
            return false
        }
        LOG.info(LOG_MARKER, "Setting village id of {} to {}", target, id)
        target.setAttached(VILLAGE_REF, id)
        return true
    }

    fun claimDeferred(
        target: AttachmentTarget,
        id: UUID,
    ) {
        LOG.info(LOG_MARKER, "Deferring village id attachment of {} to {}", target, id)
        PENDING.add(PendingClaim(target, id))
    }

    fun drainDeferred() {
        while (!PENDING.isEmpty()) {
            Option
                .of(PENDING.poll())
                .filter { p ->
                    !(
                        (p.target is BlockEntity && p.target.isRemoved) ||
                            p.target.hasAttached(
                                VILLAGE_REF,
                            )
                    )
                }.forEach { p -> claim(p.target, p.id) }
        }
    }

    private data class PendingClaim(
        val target: AttachmentTarget,
        val id: UUID,
    )
}
