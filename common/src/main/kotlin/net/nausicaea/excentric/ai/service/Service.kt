package net.nausicaea.excentric.ai.service

import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.java.util.Probability
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Need

interface Service {
    fun key(): ResourceKey<Need>

    fun prerequisite(): Prerequisite

    fun baseUtility(): Probability

    fun realise(): Probability
}
