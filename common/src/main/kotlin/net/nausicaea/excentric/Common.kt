package net.nausicaea.excentric

import net.minecraft.resources.ResourceLocation
import org.slf4j.MarkerFactory

const val MOD_ID = "excentric"

@JvmField
val LOG_MARKER = MarkerFactory.getMarker(MOD_ID)

fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
