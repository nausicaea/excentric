package net.nausicaea.excentric.minecraft.world.level;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.nausicaea.excentric.CommonKt;
import net.nausicaea.excentric.ExcentricAttachments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("UnstableApiUsage")
public final class VillageRefs {
    private static final Logger LOG = LoggerFactory.getLogger(VillageRefs.class);
    private static final Queue<PendingClaim> PENDING = new ArrayDeque<>();

    public static Optional<UUID> get(AttachmentTarget target) {
        return Optional.ofNullable(target.getAttached(ExcentricAttachments.VILLAGE_ID));
    }

    public static boolean claim(AttachmentTarget target, UUID id) {
        if (target.hasAttached(ExcentricAttachments.VILLAGE_ID)) {
            return false;
        }
        LOG.info(CommonKt.LOG_MARKER, "Setting village id of {} to {}", target, id);
        target.setAttached(ExcentricAttachments.VILLAGE_ID, id);
        return true;
    }

    public static void claimDeferred(AttachmentTarget target, UUID id) {
        LOG.info(CommonKt.LOG_MARKER, "Deferring village id attachment of {} to {}", target, id);
        PENDING.add(new PendingClaim(target, id));
    }

    public static void drainDeferred() {
        while (!PENDING.isEmpty()) {
            Optional.ofNullable(PENDING.poll())
                    .filter(
                            p ->
                                    !((p.target instanceof BlockEntity be && be.isRemoved())
                                            || p.target.hasAttached(
                                                    ExcentricAttachments.VILLAGE_ID)))
                    .ifPresent(p -> claim(p.target, p.id));
        }
    }

    private VillageRefs() {}

    private record PendingClaim(AttachmentTarget target, UUID id) {}
}
