package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.ready
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try

object Fulfilled : Prerequisite {
    override fun fulfil(entity: LivingEntity): Future<Try<Unit>> = ready(Ok(Unit))
}
