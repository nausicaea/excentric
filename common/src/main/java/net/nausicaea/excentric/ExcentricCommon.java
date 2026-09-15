package net.nausicaea.excentric;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Loader-agnostic entry point. Nothing in this class or package should import
// net.fabricmc.* (or, later, net.neoforged.*) — that keeps it reusable if a
// second loader module is ever added. Each platform module's own entrypoint
// class calls init() from its onInitialize()/mod constructor.
public final class ExcentricCommon {
	public static final String MOD_ID = "excentric";

	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private ExcentricCommon() {
	}

	public static void init() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello world, from common!");
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
