package net.nausicaea.excentric;

import java.util.UUID;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;

public final class ExcentricAttachments {
    public static final AttachmentType<UUID> VILLAGE_ID =
            AttachmentRegistry.createPersistent(CommonKt.id("village_id"), UUIDUtil.CODEC);

    private ExcentricAttachments() {}
}
