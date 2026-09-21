package net.nausicaea.excentric;

import net.minecraft.resources.ResourceLocation;

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
		LOG.info(MARKER, "Hello world, from common!");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
