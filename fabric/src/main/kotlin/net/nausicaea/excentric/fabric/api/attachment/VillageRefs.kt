package net.nausicaea.excentric.fabric.api.attachment

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget
import net.minecraft.world.level.block.entity.BlockEntity
import net.nausicaea.excentric.ExcentricAttachments
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.filter
import net.nausicaea.excentric.util.forEach
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.ArrayDeque
import java.util.Queue
import java.util.UUID

@Suppress("UnstableApiUsage")
object VillageRefs {
    private val LOG: Logger = LoggerFactory.getLogger(VillageRefs::class.java)
    private val PENDING: Queue<PendingClaim> = ArrayDeque()

    @JvmStatic
    fun get(target: AttachmentTarget): Option<UUID> = Option.of(target.getAttached(ExcentricAttachments.VILLAGE_ID))

    @JvmStatic
    fun claim(
        target: AttachmentTarget,
        id: UUID,
    ): Boolean {
        if (target.hasAttached(ExcentricAttachments.VILLAGE_ID)) {
            return false
        }
        LOG.info(LOG_MARKER, "Setting village id of {} to {}", target, id)
        target.setAttached(ExcentricAttachments.VILLAGE_ID, id)
        return true
    }

    @JvmStatic
    fun claimDeferred(
        target: AttachmentTarget,
        id: UUID,
    ) {
        LOG.info(LOG_MARKER, "Deferring village id attachment of {} to {}", target, id)
        PENDING.add(PendingClaim(target, id))
    }

    @JvmStatic
    fun drainDeferred() {
        while (!PENDING.isEmpty()) {
            Option
                .of(PENDING.poll())
                .filter { p ->
                    !(
                        (p.target is BlockEntity && p.target.isRemoved) ||
                            p.target.hasAttached(
                                ExcentricAttachments.VILLAGE_ID,
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
