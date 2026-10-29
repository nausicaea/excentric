package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Ready
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestFuture {
    @Test
    fun pendingNeverCompletes() {
        val fut: Future<Int> = pending()
        for (i in 0..100) {
            assertTrue(fut.poll().isPending())
        }
    }

    @Test
    fun readyCompletesImmediatelyThenFuses() {
        val fut = ready(Unit)
        assertTrue(fut.poll().isReady())
        assertTrue(fut.isTerminated())
        for (i in 0..100) {
            assertTrue(fut.poll().isPending())
        }
    }

    @Test
    fun andThenOkDoesNotStartTheSecondFutureIfTheFirstFails() {
        val boom = IllegalStateException("boom")
        var started = false

        val f =
            ready<Try<Unit>>(Err(boom)).andThenOk {
                started = true
                ready<Try<Unit>>(Ok(Unit))
            }

        assertEquals(Ready(Err(boom)), f.poll())
        assertFalse(started)
    }

    @Test
    fun andThenOkRunsTheSecondFutureAfterSuccess() {
        val f = ready<Try<Unit>>(Ok(Unit)).andThenOk { ready<Try<Unit>>(Ok(Unit)) }

        assertEquals(Ready(Ok(Unit)), f.poll())
    }
}
