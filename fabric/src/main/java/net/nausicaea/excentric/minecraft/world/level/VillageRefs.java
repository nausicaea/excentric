package net.nausicaea.excentric.minecraft.world.level;

import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.nausicaea.excentric.ExcentricAttachments;
import net.nausicaea.excentric.ExcentricCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public final class VillageRefs {
	private static final Logger LOG = LoggerFactory.getLogger(VillageRefs.class);

	public static Optional<UUID> get(AttachmentTarget target) {
		return Optional.ofNullable(target.getAttached(ExcentricAttachments.VILLAGE_ID));
	}

	/// Set-once: returns false if a village is already recorded.
	public static boolean claim(AttachmentTarget target, UUID id) {
		if (target.hasAttached(ExcentricAttachments.VILLAGE_ID)) {
			return false;
		}
		target.setAttached(ExcentricAttachments.VILLAGE_ID, id);
		LOG.info(ExcentricCommon.MARKER, "Setting village id of {} to {}", target, id);
		return true;
	}

	private VillageRefs() {
	}
}
