package net.nausicaea.excentric.util.task

import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestPoll {
    @Test
    fun mapOkTransformsValueOnReadyOk() {
        val res: Poll<Try<Int>> = Ready(Ok(100))
        assertEquals(Ready(Ok(101)), res.mapOk { it + 1 })
    }

    @Test
    fun mapOkSkipsTransformationAndPropagatesErrorOnReadyErr() {
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
    fun mapOkSkipsTransformationOnPending() {
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
}
