package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.andThenOk
import net.nausicaea.excentric.util.result.Try

class And<out T : Prerequisite, out U : Prerequisite>(
    val a: T,
    val b: U,
) : Prerequisite {
    override fun fulfil(entity: LivingEntity): Future<Try<Unit>> = a.fulfil(entity).andThenOk { b.fulfil(entity) }
}
