package net.nausicaea.excentric.util

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
fun interface Future<out T : Any> {
    /**
     * Attempts to resolve the future to a final value.
     *
     * # Return value
     *
     * This function returns:
     *
     * - [Poll.Pending] if the future is not ready yet
     * - [Poll.Ready] with the result of this future if it
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
}
