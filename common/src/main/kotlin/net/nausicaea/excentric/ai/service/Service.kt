package net.nausicaea.excentric.ai.service

import net.minecraft.resources.ResourceKey
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try

interface Service {
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
    fun prerequisite(): Option<Prerequisite>

    /**
     * Use the service, returning a promise that needs to be polled to completion.
     */
    fun use(): Future<Try<Probability>>
}

