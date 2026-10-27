package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.Ok
import kotlin.test.Test
import kotlin.test.assertTrue

class TestFuture {
    @Test
    fun pendingNeverCompletes() {
        val fut = pending()
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
}
