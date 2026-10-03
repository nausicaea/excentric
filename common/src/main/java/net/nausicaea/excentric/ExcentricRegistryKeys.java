package net.nausicaea.excentric;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.minecraft.world.entity.ai.Need;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcentricRegistryKeys {
	private static final Logger LOG = LoggerFactory.getLogger(ExcentricRegistryKeys.class);
	public static final ResourceKey<Registry<Need>> NEED = ResourceKey.createRegistryKey(ExcentricCommon.id("need"));

	private ExcentricRegistryKeys() {
	}

	public static void register() {
		LOG.info(ExcentricCommon.MARKER, "Creating custom registry keys");
	}
}
