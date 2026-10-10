package net.nausicaea.excentric.ai.service

import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.util.Probability

interface Service {
    fun key(): ResourceKey<Need>

    fun prerequisite(): Prerequisite

    fun baseUtility(): Probability

    fun realise(): Probability
}
