package net.nausicaea.excentric.ai.prerequisite

/**
 * Denotes something that needs to be satisfied as a pre-condition to something
 * else. The interface is modeled after a `Promise` or
 * [java.util.concurrent.Future].
 */
interface Prerequisite {
    /**
     * Poll the [Prerequisite], bringing it closer to satisfaction. Return the
     * current [PrerequisiteState].
     */
    @Suppress("SameReturnValue")
    fun poll(): PrerequisiteState
}

/**
 * The state of the [Prerequisite] after [Prerequisite.poll] was last called.
 */
enum class PrerequisiteState {
    PENDING,
    SATISFIED,
    FAILED,
}
