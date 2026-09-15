package net.nausicaea.excentric;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcentricCommon {
	public static final String MOD_ID = "excentric";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private ExcentricCommon() {
	}

	public static void init() {
		LOGGER.info("Hello world, from common!");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
