package net.nausicaea.excentric.ai.service

import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.ai.reasoner.Action
import net.nausicaea.excentric.util.Probability

interface Service : Action {
    /**
     * Which [Need] this service aims to satisfy.
     */
    fun key(): ResourceKey<Need>

    /**
     * How attractive this service is at face value.
     */
    fun baseUtility(): Probability

    /**
     * Does the service have some precondition that needs to be met?
     */
    fun prerequisite(): Prerequisite?
}
