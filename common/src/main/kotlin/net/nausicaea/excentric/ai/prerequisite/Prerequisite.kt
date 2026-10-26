package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.andThen

sealed interface Prerequisite {
    fun fulfil(entity: LivingEntity): Future<Try<Unit>>
}

fun Prerequisite.and(p: Prerequisite): And = And(this, p)

class And(
    val a: Prerequisite,
    val b: Prerequisite,
) : Prerequisite {
    override fun fulfil(entity: LivingEntity): Future<Try<Unit>> = a.fulfil(entity).andThen { b.fulfil(entity) }
}
