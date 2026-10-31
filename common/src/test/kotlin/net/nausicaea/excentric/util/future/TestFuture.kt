package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.future.FutureExt.andThenOk
import net.nausicaea.excentric.util.future.FutureExt.cancellableOk
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

private class CloseCounter : AutoCloseable {
    private var closes = 0

    val closeInvocations: Int get() = closes

    override fun close() {
        closes += 1
    }
}

/**
 * Continuously return a ready future.
 */
private fun repeat(): Future<Unit> = { Ready(Unit) }

/**
 * Count the number of [Future.poll] invocations of the underlying future
 */
private fun <T : Any> Future<T>.countInvocations(): CountInvocations<T> = CountInvocations(this)

private class CountInvocations<T : Any>(
    private var fut: Future<T>?,
) : FusedFuture<T> {
    private var polls: Int = 0
    private var closes: Int = 0

    val pollInvocations: Int get() = polls
    val closeInvocations: Int get() = closes

    override fun poll(): Poll<T> {
        polls += 1
        return fut?.poll() ?: Pending
    }

    override fun close() {
        closes += 1
        fut?.close()
        fut = null
    }

    override fun isTerminated(): Boolean = fut == null
}

private fun <T : Any> assertPendingForevermore(fut: Future<T>) {
    @Suppress("ForEachParameterNotUsed")
    (0..100).forEach { assertPending(fut.poll()) }
}

private fun <T : Any> assertPending(poll: Poll<T>) {
    assertIs<Pending>(poll)
}

private fun <T : Any> assertReady(
    expected: T,
    poll: Poll<T>,
) {
    assertEquals(Ready(expected), poll)
}

private fun <T : Any> assertReadyOk(
    expected: T,
    poll: Poll<Try<T>>,
) {
    assertIs<Ready<Try<T>>>(poll)
    assertIs<Ok<T>>(poll.value)
    assertEquals(expected, poll.value.value)
}

private inline fun <reified E : Exception, T : Any> assertReadyErr(poll: Poll<Try<T>>) {
    assertIs<Ready<Try<T>>>(poll)
    assertIs<Err>(poll.value)
    assertIs<E>(poll.value.value)
}

private fun <T : Any> assertFused(
    fut: FusedFuture<T>,
    expected: (FusedFuture<T>) -> Unit,
) {
    assertFalse(fut.isTerminated())
    expected(fut)
    assertTrue(fut.isTerminated())
    assertPendingForevermore(fut)
}

class TestFuture {
    @Test
    fun `pending always returns Pending`() {
        assertPendingForevermore(pending<Int>())
    }

    @Test
    fun `ready is fused and returns Ready exactly once`() {
        assertFused(ready(Unit)) { assertReady(Unit, it.poll()) }
    }

    @Test
    fun `ready does not clean up resources on outer close`() {
        val value = CloseCounter()
        ready(value).close()
        assertEquals(0, value.closeInvocations)
    }

    @Test
    fun `ready does not clean up resources when Ready`() {
        val value = CloseCounter()
        ready(value).poll()
        assertEquals(0, value.closeInvocations)
    }

    @Test
    fun `fused causes a future to return Ready at most once and Pending afterwards while the inner future is only called until Ready`() {
        val rep = repeat().countInvocations()
        assertFused(rep.fuse()) { assertReady(Unit, it.poll()) }
        assertEquals(1, rep.pollInvocations)
        assertEquals(1, rep.closeInvocations)
    }

    @Test
    fun `fused calls close on inner future on outer close`() {
        val rep = repeat().countInvocations()
        rep.fuse().close()
        assertEquals(1, rep.closeInvocations)
    }

    @Test
    fun `andThenOk does not start the second future while the first is Pending`() {
        var invocations = 0
        val f =
            pending<Try<Unit>>().andThenOk {
                invocations += 1
                ready(Ok(1))
            }
        assertPendingForevermore(f)
        assertEquals(0, invocations)
    }

    @Test
    fun `andThenOk does not start the second future if the first fails`() {
        var invoked = false
        val fut =
            ready<Try<Unit>>(Err(RuntimeException()))
                .andThenOk {
                    invoked = true
                    ready(Ok(1))
                }
        assertReadyErr<RuntimeException, _>(fut.poll())
        assertFalse(invoked)
    }

    @Test
    fun `andThenOk is fused`() {
        val first = ready<Try<Unit>>(Ok(Unit)).countInvocations()
        val second = ready<Try<Int>>(Ok(1)).countInvocations()
        val fut = first.andThenOk { second }
        assertFused(fut) { assertReadyOk(1, it.poll()) }
        assertEquals(1, first.pollInvocations)
        assertEquals(1, first.closeInvocations)
        assertEquals(1, second.pollInvocations)
        assertEquals(1, second.closeInvocations)
    }

    @Test
    fun `andThenOk calls close on first inner future on outer close when second has not been called yet`() {
        val first = ready<Try<Unit>>(Ok(Unit)).countInvocations()
        val second = ready<Try<Int>>(Ok(1)).countInvocations()
        first.andThenOk { second }.close()
        assertEquals(1, first.closeInvocations)
        assertEquals(0, second.closeInvocations)
    }

    @Test
    fun `andThenOk calls close on both inner futures on outer close when second was called at least once`() {
        val first = ready<Try<Unit>>(Ok(Unit)).countInvocations()
        val second = ready<Try<Int>>(Ok(1)).countInvocations()
        val fut = first.andThenOk { second }
        fut.poll()
        fut.close()
        assertEquals(1, first.closeInvocations)
        assertEquals(1, second.closeInvocations)
    }

    @Test
    fun `andThen does not start the second future while the first is Pending`() {
        var invocations = 0
        val f =
            pending<Unit>().andThen {
                invocations += 1
                ready(1)
            }
        assertPendingForevermore(f)
        assertEquals(0, invocations)
    }

    @Test
    fun `andThen is fused`() {
        val first = ready(Unit).countInvocations()
        val second = ready(1).countInvocations()
        val fut = first.andThen { second }
        assertFused(fut) { assertReady(1, it.poll()) }
        assertEquals(1, first.pollInvocations)
        assertEquals(1, first.closeInvocations)
        assertEquals(1, second.pollInvocations)
        assertEquals(1, second.closeInvocations)
    }

    @Test
    fun `cancellableOk is fused`() {
        val pnd = pending<Try<Unit>>().countInvocations()
        var cancel = false
        val fut = pnd.cancellableOk { cancel }
        assertFused(fut) {
            cancel = true
            assertReadyErr<Cancelled, _>(it.poll())
        }
        assertEquals(1, pnd.closeInvocations)
    }

    @Test
    fun `cancellableOk never polls inner future when cancelled immediately`() {
        val pnd = pending<Try<Unit>>().countInvocations()
        pnd.cancellableOk { true }.poll()
        assertEquals(0, pnd.pollInvocations)
    }

    @Test
    fun `cancellableOk calls close on inner future on outer close`() {
        val pnd = pending<Try<Unit>>().countInvocations()
        val cancel = false
        pnd.cancellableOk { cancel }.close()
        assertEquals(1, pnd.closeInvocations)
    }

    @Test
    fun `cancellable is fused`() {
        val pnd = pending<Unit>().countInvocations()
        var cancel = false
        val fut = pnd.cancellable { cancel }

        assertFused(fut) {
            cancel = true
            assertReadyErr<Cancelled, _>(it.poll())
        }
        assertEquals(1, pnd.closeInvocations)
    }

    @Test
    fun `cancellable never polls inner future when cancelled immediately`() {
        val pnd = pending<Unit>().countInvocations()
        pnd.cancellable { true }.poll()
        assertEquals(0, pnd.pollInvocations)
    }

    @Test
    fun `cancellable calls close on inner future on outer close`() {
        val pnd = pending<Unit>().countInvocations()
        val cancel = false
        pnd.cancellable { cancel }.close()
        assertEquals(1, pnd.closeInvocations)
    }
}
