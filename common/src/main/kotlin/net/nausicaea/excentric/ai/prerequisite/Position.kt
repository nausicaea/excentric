package net.nausicaea.excentric.ai.prerequisite

import net.minecraft.world.phys.Vec3

/**
 * Denotes a [Prerequisite] position: the entity that wants to satisfy this
 * condition must navigate to the stored [Vec3].
 */
data class Position(
    /**
     * The target position that needs to be navigated to.
     */
    val pos: Vec3,
    /**
     * Error term for position comparisons. Determines the maximum deviation from the target position that is acceptable for pathfinding.
     */
    val error: Int = 1,
) : Prerequisite
