package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.phys.Vec3

/**
 * Denotes a [Prerequisite] position: the entity that wants to satisfy this
 * condition must navigate to the stored [Vec3].
 */
data class Position(
    val pos: Vec3,
) : Prerequisite {
    override fun poll(): PrerequisiteState {
        TODO("Not yet implemented")
    }
}
