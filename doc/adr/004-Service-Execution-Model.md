---
state: accepted
owner: nausicaea
acceptance_date: 2026-10-28
modification_date: 2026-10-28
---

# Decision

* We will use the constructs `Try`, `Ok(...)`, and `Err(...)` to represent the results from fallible
  computations with an error. Combinators such as `map` or `andThen` will be used to operate on the wrapped values.
  There must be operations that flatten nested wrapped errors.
* We will use the constructs `Opt`, `Some(...)`, and `None` as a functional representations of an optional value. An absent
  value (`None`) is not equivalent to an error (`Err(...)`). Combinators such as `map` or `andThen` will be used to
  operate on the wrapped values.
* We will use the construct `Future` to represent a computation with a deferred result. The method `Future.poll` asks
  the implementor if a result is ready. `Future` implements `AutoCloseable` and provides a default empty `Future.close`
  implementation. `Future.close` is to be called explicitly on cancellation of a `Future`. Combinators such as `andThen`
  allow chaining and modifying deferred computations before or once they are ready.
* We will denote the availability of a result from a future with the constructs `Poll`, `Ready(...)`, and `Pending`.
  Combinators such as `map` will be used to operate on the wrapped values.
* We will forego introducing a special construct for fallible futures. Instead, one shall use `Future<Try<T>>`.
  Combinators such as `andThenOk` shall transform the wrapped values depending on success or failure of the combination,
  and errors shall propagate.

# Alternatives that were Evaluated

* Constructs similar to Minecraft's `Goal` or `Behavior` that carry `start()`, `tick()`, and `stop()` methods in favour
  of constructs that offer combinators.
* Concurrent programming techniques such as Kotlin's `suspend` functions. I don't want `async`-pollution in my Mod and I
  want to restrict the use of deferred computation to specific narrow areas, such as the utility AI evaluation.
