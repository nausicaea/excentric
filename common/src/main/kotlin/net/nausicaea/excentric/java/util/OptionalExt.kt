package net.nausicaea.excentric.java.util

import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Option.None
import net.nausicaea.excentric.util.Option.Some
import java.util.Optional

fun <T> Optional<T>.toOption(): Option<T> =
    if (this.isPresent) {
        Some(this.get())
    } else {
        None
    }
