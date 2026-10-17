package net.nausicaea.excentric.ai.service

import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.inspect
import net.nausicaea.excentric.util.map

interface ServiceFuture : Future<Try<Probability>> {
    /**
     * Attempt to bring the [Future] closer to completion.
     *
     * @throws IllegalStateException if polled again once [Poll.Ready] has been returned.
     */
    override fun poll(): Poll<Try<Probability>> {
        if (ready()) {
            throw IllegalStateException("Cannot poll a complete Future")
        }
        return when (val prq = prerequisite()) {
            null -> {
                setReady()
                Poll.Ready(Try.Ok(useNow()))
            }

            is Position -> {
                prq
                    .poll(consumer())
                    .inspect { setReady() }
                    .map { Try.Ok(useNow()) }
            }

            else -> {
                setReady()
                Poll.Ready(Try.Err(UnsupportedOperationException("Unsupported prerequisite: $prq")))
            }
        }
    }

    fun prerequisite(): Prerequisite?

    fun consumer(): LivingEntity

    fun ready(): Boolean

    fun setReady()

    fun useNow(): Probability
}
