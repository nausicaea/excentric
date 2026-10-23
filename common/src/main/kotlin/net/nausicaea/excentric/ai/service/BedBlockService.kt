package net.nausicaea.excentric.ai.service

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.LivingEntity
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.RESTEDNESS
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Try
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * TODO: currently supports going to sleep only. Implement waking up.
 */
data class BedBlockService(
    val pos: BlockPos,
    val ctx: BlockServiceContext,
) : Service {
    override fun key(): ResourceKey<Need> =
        NEED_REGISTRY
            .getResourceKey(RESTEDNESS)
            .orElseThrow {
                IllegalStateException("Cannot find resource key for builtin need 'restedness'")
            }

    /**
     * TODO: make a bed's base utility depend on the occupation or reservation status
     */
    override fun baseUtility(): Probability = Probability.ONE

    override fun prerequisite(): Prerequisite = Position(pos.center)

    override fun describe(): String = "bed at ${pos.toShortString()}"

    override fun apply(): Future<Try<Probability>> = BedBlockFuture(ctx, pos, prerequisite())

    class BedBlockFuture(
        val ctx: BlockServiceContext,
        val pos: BlockPos,
        val prq: Prerequisite?,
        var ready: Boolean = false,
    ) : ServiceFuture {
        override fun prerequisite(): Prerequisite? = prq

        override fun consumer(): LivingEntity = ctx.consumer

        override fun ready(): Boolean = ready

        override fun setReady() {
            ready = true
        }

        override fun useNow(): Probability {
            LOG.info(LOG_MARKER, "Start sleeping")
            ctx.consumer.startSleeping(pos)
            // When the entity starts sleeping, there is no benefit just yet.
            return Probability.ZERO
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(BedBlockFuture::class.java)
    }
}
