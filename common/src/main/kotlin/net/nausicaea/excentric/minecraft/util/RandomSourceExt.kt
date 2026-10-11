package net.nausicaea.excentric.minecraft.util

import net.minecraft.util.RandomSource
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Option.None
import net.nausicaea.excentric.util.Weighted
import kotlin.require

/**
 * Perform a weighted random selection from a list.
 */
fun <T> RandomSource.chooseWeighted(items: List<Weighted<T>>): Option<T> {
    var total = 0.0
    for ((_, weight1) in items) {
        val weight = weight1.value
        require(!(!(weight >= 0.0) || weight.isInfinite())) { "Invalid weight: $weight" }
        total += weight
    }
    if (total <= 0.0) {
        return None
    }

    var r: Double = this.nextDouble() * total
    var lastPositive: T? = null
    for ((item, weight1) in items) {
        val weight = weight1.value
        if (weight <= 0.0) continue
        r -= weight
        if (r < 0.0) {
            return Option.of(item)
        }
        lastPositive = item
    }
    return Option.of(lastPositive)
}
