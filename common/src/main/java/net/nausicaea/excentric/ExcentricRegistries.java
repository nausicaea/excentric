package net.nausicaea.excentric;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.nausicaea.excentric.minecraft.world.entity.ai.Need;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcentricRegistries {
	private static final Logger LOG = LoggerFactory.getLogger(ExcentricRegistries.class);

	private ExcentricRegistries() {
	}

	public static final MappedRegistry<Need> NEEDS = new MappedRegistry<>(ExcentricRegistryKeys.NEED,
	    Lifecycle.stable());

	public static void register() {
		LOG.info(ExcentricCommon.MARKER, "Creating custom registries");
	}
}
