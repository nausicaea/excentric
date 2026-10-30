package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.future.FutureExt.andThenOk
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

/**
 * Continuously return a ready future.
 */
private fun repeat(): Future<Unit> = { Ready(Unit) }

/**
 * Count the number of [Future.poll] invocations
 */
private fun <T : Any> Future<T>.countInvocations(): CountInvocations<T> = CountInvocations(this)

private class CountInvocations<T : Any>(
    private val fut: Future<T>,
) : Future<T> {
    private var c: Int = 0

    val invocations: Int get() = c

    override fun poll(): Poll<T> {
        c += 1
        return fut.poll()
    }
}

/**
 * Count the number of [FusedFuture.poll] invocations of the underlying future
 */
private fun <T : Any> FusedFuture<T>.countInvocations(): FusedCountInvocations<T> = FusedCountInvocations(this)

private class FusedCountInvocations<T : Any>(
    private val fut: FusedFuture<T>?,
) : FusedFuture<T> {
    private var c: Int = 0

    val invocations: Int get() = c

    override fun poll(): Poll<T> =
        fut?.let {
            c += 1
            it.poll()
        } ?: Pending

    override fun isTerminated(): Boolean = fut?.isTerminated() ?: true
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
    fun `fused causes a future to return Ready at most once and Pending afterwards while the inner future is only called until Ready`() {
        val rep = repeat().countInvocations()
        assertFused(rep.fuse()) { assertReady(Unit, it.poll()) }
        assertEquals(1, rep.invocations)
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
        assertEquals(1, first.invocations)
        assertEquals(1, second.invocations)
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
        assertEquals(1, first.invocations)
        assertEquals(1, second.invocations)
    }

    @Test
    fun `cancellable is fused and closes the future from a predicate`() {
        val pnd = pending<Unit>()
        var cancel = false
        val fut = pnd.cancellable { cancel }

        assertFused(fut) {
            cancel = true
            assertReadyErr<Cancelled, _>(it.poll())
        }
    }
}
