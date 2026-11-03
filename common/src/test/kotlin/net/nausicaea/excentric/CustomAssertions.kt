package net.nausicaea.excentric

import net.nausicaea.excentric.util.future.FusedFuture
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Err
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

fun <T : Any> assertPendingForevermore(fut: Future<T>) {
    @Suppress("ForEachParameterNotUsed")
    (0..100).forEach { assertPending(fut.poll()) }
}

fun <T : Any> assertPending(poll: Poll<T>) {
    assertIs<Pending>(poll)
}

fun <T : Any> assertReady(
    expected: T,
    poll: Poll<T>,
) {
    assertEquals(Ready(expected), poll)
}

fun <T : Any> assertReadyOk(
    expected: T,
    poll: Poll<Try<T>>,
) {
    assertIs<Ready<Try<T>>>(poll)
    assertIs<Ok<T>>(poll.value)
    assertEquals(expected, poll.value.value)
}

inline fun <reified E : Exception, T : Any> assertReadyErr(poll: Poll<Try<T>>) {
    assertIs<Ready<Try<T>>>(poll)
    assertIs<Err>(poll.value)
    assertIs<E>(poll.value.value)
}

fun <T : Any> assertFused(
    fut: FusedFuture<T>,
    expected: (FusedFuture<T>) -> Unit,
) {
    assertFalse(fut.isTerminated())
    expected(fut)
    assertTrue(fut.isTerminated())
    assertPendingForevermore(fut)
}
