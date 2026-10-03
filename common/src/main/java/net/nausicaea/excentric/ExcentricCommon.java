package net.nausicaea.excentric;

import net.minecraft.resources.ResourceLocation;

import net.nausicaea.excentric.minecraft.world.entity.ai.need.Needs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class ExcentricCommon {
	public static final String MOD_ID = "excentric";
	public static final Marker MARKER = MarkerFactory.getMarker(MOD_ID);
	public static final Logger LOG = LoggerFactory.getLogger(ExcentricCommon.class);

	private ExcentricCommon() {
	}

	public static void onInitialize() {
		ExcentricRegistryKeys.register();
		ExcentricRegistries.register();
		Needs.register();
		LOG.info(MARKER, "Testing seed: -5038984067013596602");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
