package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready
import org.jetbrains.annotations.Contract

/**
 * A future represents an asynchronous computation.
 *
 * A future is a value that might not have finished computing yet. This kind of
 * "asynchronous value" makes it possible for a thread to continue doing useful
 * work while it waits for the value to become available.
 *
 * # Differences to other APIs
 *
 * Compared to Rust's `core::future::Future` or Java's [java.util.concurrent.Future], this future is **not** designed to be called in I/O-bound contexts or across thread boundaries. It is merely an abstraction for a deferred result.
 *
 * # The `poll` method
 *
 * The core method of future, [poll], *attempts* to resolve the future into a
 * final value. This method does not block if the value is not ready.
 */
fun interface Future<T : Any> : AutoCloseable {
    /**
     * Attempts to resolve the future to a final value.
     *
     * # Return value
     *
     * This function returns:
     *
     * - [Pending] if the future is not ready yet
     * - [Ready] with the result of this future if it
     *   finished successfully.
     *
     * Once a future has finished, clients should not call [poll] again.
     *
     * # Runtime characteristics
     *
     * Futures alone are *inert*; they must be *actively* `poll`ed for the
     * underlying computation to make progress, meaning that each time the
     * current task is woken up, it should actively re-`poll` pending futures
     * that it still has an interest in.
     *
     * An implementation of `poll` should strive to return quickly, and should
     * not block. Returning quickly prevents unnecessarily clogging up event loops. If it is known ahead of time that a call to
     * `poll` may end up taking a while, the work should be offloaded to a
     * thread pool (or something similar) to ensure that `poll` can return
     * quickly.
     *
     * # Panics
     *
     * Once a future has completed (returned `Ready` from `poll`), calling its
     * `poll` method again may throw, block forever, or cause other kinds of
     * problems; the `Future` interface places no requirements on the effects of
     * such a call.
     */
    fun poll(): Poll<T>

    /**
     * Abort the future, cleaning up all acquired resources. Must be idempotent and implementors may not throw exceptions.
     */
    override fun close() {}

    fun <U : Any> map(f: (T) -> U): Map<T, U> = Map(this, f)
}

/**
 * Creates a future that is immediately ready with a value.
 */
fun <T : Any> ready(t: T): ReadyFut<T> = ReadyFut(t)

/**
 * Creates a future which never resolves, representing a computation that never finishes.
 */
fun <T : Any> pending(): Future<T> = { Pending }

/**
 * Chain on a computation for when a future finished, passing the result of the future to the provided closure f.
 *
 * The returned value of the closure must implement the Future trait and can represent some more work to be done before the composed future is finished.
 *
 * The closure f is only run after successful completion of the self future.
 */
fun <T : Any, U : Any> Future<T>.andThen(f: (T) -> Future<U>): AndThen<T, U> = AndThen(this, f)

fun <T : Any, U : Any> Future<Try<T>>.andThenOk(f: (T) -> Future<Try<U>>): AndThenOk<T, U> = AndThenOk(this, f)

fun <T : Any> Future<T>.cancellable(f: () -> Boolean): Cancellable<T> = Cancellable(this, f)

fun <T : Any> Future<Try<T>>.cancellableOk(f: () -> Boolean): CancellableOk<T> = CancellableOk(this, f)

fun <T : Any> Future<T>.fuse(): Fused<T> = Fused(this)
