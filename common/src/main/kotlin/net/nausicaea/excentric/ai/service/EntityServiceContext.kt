package net.nausicaea.excentric.ai.service

import net.minecraft.world.entity.LivingEntity

data class EntityServiceContext(
    val consumer: LivingEntity,
)
