package net.nausicaea.excentric.ai.prerequisite

import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.ready
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try

object Fulfilled : Prerequisite {
    override fun <T : Any> fulfil(subject: T): Future<Try<Unit>> = ready(Ok(Unit))
}
