package net.nausicaea.excentric.ai.need

import net.minecraft.core.Registry
import net.nausicaea.excentric.id
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY

/**
 * See [Satiation] for information.
 */
@JvmField
val SATIATION: Need =
    Registry.register(
        NEED_REGISTRY,
        id("satiation"),
        Satiation,
    )

/**
 * See [Restedness] for information.
 */
@JvmField
val RESTEDNESS: Need =
    Registry.register(
        NEED_REGISTRY,
        id("restedness"),
        Restedness,
    )
