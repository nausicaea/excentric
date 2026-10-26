package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Ok
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.ready

object Fulfilled : Prerequisite {
    override fun fulfil(entity: LivingEntity): Future<Try<Unit>> = ready(Ok(Unit))
}
