package net.nausicaea.excentric.util.task

import net.nausicaea.excentric.assertPending
import net.nausicaea.excentric.assertReady
import net.nausicaea.excentric.assertReadyErr
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestPoll {
    @Test
    fun `mapOk transforms value on ReadyOk`() {
        val res: Poll<Try<Int>> = Ready(Ok(100))
        assertEquals(Ready(Ok(101)), res.mapOk { it + 1 })
    }

    @Test
    fun `mapOk skips transformation and propagates error on ReadyErr`() {
        val exc = RuntimeException("boom")
        var invoked = false
        val res: Poll<Try<Int>> = Ready(Err(exc))
        assertEquals(
            Ready(Err(exc)),
            res.mapOk {
                invoked = true
                it + 1
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `mapOk skips transformation on Pending`() {
        var invoked = false
        val res: Poll<Try<Int>> = Pending
        assertEquals(
            Pending,
            res.mapOk {
                invoked = true
                it + 1
            },
        )
        assertFalse(invoked)
    }

    @Test
    fun `orElse evaluates a lambda on Pending`() {
        assertReadyErr<RuntimeException, Unit>(Pending.orElse { Ready(Err(RuntimeException())) })
        assertPending<Unit>(Pending.orElse { Pending })
    }

    @Test
    fun `orElse skips lambda on Ready`() {
        assertReadyErr<IllegalStateException, Unit>(Ready(Err(IllegalStateException())).orElse { Ready(Err(RuntimeException())) })
        assertReadyErr<IllegalStateException, Unit>(Ready(Err(IllegalStateException())).orElse { Pending })
    }

    @Test
    fun `inspectPending evaluates a lambda on Pending`() {
        var invoked = false
        assertPending(Pending.inspectPending { invoked = true })
        assertTrue(invoked)
    }

    @Test
    fun `inspectPending skips lambda on Ready`() {
        var invoked = false
        assertReady(Unit, Ready(Unit).inspectPending { invoked = true })
        assertFalse(invoked)
    }
}
