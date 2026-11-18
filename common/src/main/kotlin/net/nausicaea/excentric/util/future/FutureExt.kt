package net.nausicaea.excentric.util.future

import net.nausicaea.excentric.util.result.Try

object FutureExt {
    fun <T : Any, U : Any> Future<Try<T>>.andThenOk(f: (T) -> Future<Try<U>>): AndThenOk<T, U> = AndThenOk(this, f)

    fun <T : Any> Future<Try<T>>.cancellableOk(f: () -> Boolean): CancellableOk<T> = CancellableOk(this, f)
}
