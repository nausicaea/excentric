package net.nausicaea.excentric.ai.service

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.Mob
import net.minecraft.world.food.FoodProperties
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.SATIATION
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Poll.Ready
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.of
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.Try.Ok
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import kotlin.math.roundToInt

private const val MAX_NUTRITION: Double = 20.0

data class SatiationItemService(
    val props: FoodProperties,
    val ctx: ItemServiceContext,
) : Service {
    override fun key(): ResourceKey<Need> =
        NEED_REGISTRY
            .getResourceKey(SATIATION)
            .orElseThrow<RuntimeException?>(
                Supplier {
                    RuntimeException(
                        "Cannot find resource key for builtin need 'satiation'",
                    )
                },
            )

    override fun prerequisite(): Prerequisite? =
        when (ctx.source) {
            // FIXME: Position isn't the only prerequisite for ForeignInventory
            is StackSource.ForeignInventory -> Position(ctx.source.pos)

            is StackSource.Held -> null

            is StackSource.Inventory -> null

            is StackSource.World -> Position(ctx.source.entity.position())
        }

    override fun baseUtility(): Probability = of(props.nutrition().toDouble() / MAX_NUTRITION)

    override fun apply(): Future<Try<Probability>> = SatiationItemFuture(props, ctx, prerequisite())
}

class SatiationItemFuture(
    val props: FoodProperties,
    val ctx: ItemServiceContext,
    val prq: Prerequisite?,
    var ready: Boolean = false,
) : Future<Try<Probability>> {
    override fun poll(): Poll<Try<Probability>> {
        if (ready) {
            throw IllegalStateException("Cannot poll a complete Future")
        }
        val prq =
            prq ?: run {
                ready = true
                return Ready(Ok(useNow(props, ctx)))
            }
        return when (prq) {
            is Position -> {
                val pos = prq.pos
                // Is the consumer at the prerequisite position?
                if (ctx.consumer.position().closerThan(pos, prq.error.toDouble())) {
                    ready = true
                    return Ready(Ok(useNow(props, ctx)))
                }

                // Move the consumer closer to the prerequisite position
                if (ctx.consumer is Mob) {
                    val nav = ctx.consumer.navigation
                    // Start the pathfinding
                    if (!(nav.isInProgress || nav.isStuck)) {
                        nav
                            .createPath(pos.x, pos.y, pos.z, prq.error)
                            ?.let { nav.moveTo(it, 1.0) }
                    }
                    Poll.Pending
                } else {
                    Ready(
                        Try.Err(
                            UnsupportedOperationException("Cannot path-find for entities that don't inherit from Mob"),
                        ),
                    )
                }
            }

            else -> {
                Ready(Try.Err(UnsupportedOperationException("Unsupported prerequisite: $prq")))
            }
        }
    }

    private fun useNow(
        props: FoodProperties,
        ctx: ItemServiceContext,
    ): Probability {
        val itemStack = ctx.itemStack()
        if (itemStack.isEmpty || !itemStack.has(DataComponents.FOOD)) {
            LOG.info(LOG_MARKER, "Non-food item or item vanished, no benefit")
            return Probability.ZERO
        }
        val consumer = ctx.consumer
        val remainder = itemStack.finishUsingItem(consumer.level(), consumer)
        ctx.putBack(remainder)
        val benefit = props.nutrition().toDouble() / MAX_NUTRITION
        LOG.info(LOG_MARKER, "Food-item provides $benefit benefit")
        return of(benefit)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SatiationItemFuture::class.java)
    }
}
