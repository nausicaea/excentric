package net.nausicaea.excentric.minecraft.core.registries

import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ExcentricCommon
import net.nausicaea.excentric.ai.need.Need

val NEED_KEY: ResourceKey<Registry<Need>> = ResourceKey.createRegistryKey(ExcentricCommon.id("need"))

@JvmField
val NEED_REGISTRY = MappedRegistry(NEED_KEY, Lifecycle.stable())
