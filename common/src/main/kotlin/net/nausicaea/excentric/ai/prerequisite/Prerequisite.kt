package net.nausicaea.excentric.ai.prerequisite

import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Try

interface Prerequisite {
    fun <T : Any> fulfil(subject: T): Future<Try<Unit>>
}
