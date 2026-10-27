package net.nausicaea.excentric.ai.service

import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.future.pending
import net.nausicaea.excentric.util.future.ready
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Ready
import kotlin.test.Test
import kotlin.test.assertEquals

class TestServiceFuture {
    @Test
    fun failedPrerequisiteIsPropagatedAndTheServiceIsNotUsed() {
        val boom = IllegalStateException("boom")
        val f = FakeServiceFuture(ready<Try<Unit>>(Err(boom)))

        assertEquals(Ready(Err(boom)), f.poll())
        assertEquals(0, f.uses)
    }

    @Test
    fun successfulPrerequisiteUsesTheServiceExactlyOnce() {
        val f = FakeServiceFuture(ready<Try<Unit>>(Ok(Unit)))

        assertEquals(Ready(Ok(Probability.of(0.5))), f.poll())
        assertEquals(1, f.uses)
    }

    @Test
    fun pendingPrerequisiteDoesNotUseTheService() {
        val f = FakeServiceFuture(pending())

        assertEquals(Pending, f.poll())
        assertEquals(0, f.uses)
    }
}

private class FakeServiceFuture(
    prq: Future<Try<Unit>>,
) : ServiceFuture(prq) {
    var uses = 0

    override fun hasExpired(): Boolean = false

    override fun useNow(): Probability {
        uses += 1
        return Probability.of(0.5)
    }
}
