package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Try

interface Prerequisite {
    fun fulfil(entity: LivingEntity): Future<Try<Unit>>
}

fun <T : Prerequisite, U : Prerequisite> T.and(p: U): And<T, U> = And(this, p)
