package net.nausicaea.excentric;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;

import java.util.UUID;

public final class ExcentricAttachments {
	public static final AttachmentType<UUID> VILLAGE_ID = AttachmentRegistry
	    .createPersistent(ExcentricCommon.id("village_id"), UUIDUtil.CODEC);

	private ExcentricAttachments() {
	}
}
