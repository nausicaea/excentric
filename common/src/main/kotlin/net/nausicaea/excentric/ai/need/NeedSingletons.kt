package net.nausicaea.excentric.ai.need

import net.minecraft.core.Registry
import net.nausicaea.excentric.ExcentricCommon
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY

@JvmField
val SATIATION: Need =
    Registry.register(
        NEED_REGISTRY,
        ExcentricCommon.id("satiation"),
        Satiation(),
    )
