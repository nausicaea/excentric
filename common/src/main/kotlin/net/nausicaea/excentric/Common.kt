package net.nausicaea.excentric

import net.minecraft.resources.ResourceLocation
import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * The mod's public identifier.
 */
const val MOD_ID = "excentric"

/**
 * Used to mark all log messages related to the mod itself.
 */
val LOG_MARKER: Marker = MarkerFactory.getMarker(MOD_ID)

/**
 * Construct a [ResourceLocation] using the mod's namespace.
 */
fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
