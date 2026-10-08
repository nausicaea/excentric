package net.nausicaea.excentric.ai.prerequisite

/**
 * Denotes a [Prerequisite] that is always satisfied.
 */
object Empty : Prerequisite {
    override fun poll(): PrerequisiteState = PrerequisiteState.SATISFIED
}
