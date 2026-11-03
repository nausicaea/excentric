package net.nausicaea.excentric.minecraft.core

import net.minecraft.core.Registry
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.Restedness
import net.nausicaea.excentric.ai.need.Satiation
import net.nausicaea.excentric.id
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY

/**
 * See [net.nausicaea.excentric.ai.need.Satiation] for information.
 */
@JvmField
val SATIATION: Need =
    Registry.register(
        NEED_REGISTRY,
        id("satiation"),
        Satiation,
    )

/**
 * See [net.nausicaea.excentric.ai.need.Restedness] for information.
 */
@JvmField
val RESTEDNESS: Need =
    Registry.register(
        NEED_REGISTRY,
        id("restedness"),
        Restedness,
    )
