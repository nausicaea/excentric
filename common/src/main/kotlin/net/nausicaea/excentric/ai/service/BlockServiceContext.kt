package net.nausicaea.excentric.ai.service

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.ai.NeedsCollection

data class BlockServiceContext(
    val subject: LivingEntity,
    val needs: NeedsCollection,
)
