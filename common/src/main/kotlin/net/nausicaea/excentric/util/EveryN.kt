package net.nausicaea.excentric.util

/**
 * Staggers a call to [EveryN.run] to an interval, calling
 * [Runnable] only once every `n` ticks on [EveryN].
 *
 * Offset multiple instances to not fall on the same tick phase.
 */
class EveryN
    @JvmOverloads
    constructor(
        val interval: Int,
        offset: Int = 0,
    ) {
        private var counter: Int = offset.mod(interval)

        /**
         * Call a [Runnable] only once every [EveryN.interval].
         */
        fun run(tickFn: Runnable) {
            counter += 1
            if (counter >= interval) {
                counter = 0
                tickFn.run()
            }
        }
    }
