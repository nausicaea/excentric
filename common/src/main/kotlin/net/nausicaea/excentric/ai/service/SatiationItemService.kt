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
import net.nausicaea.excentric.util.inspect
import net.nausicaea.excentric.util.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import kotlin.math.roundToInt

private const val MAX_NUTRITION: Double = 20.0

/**
 * Defines the a [Service] for [net.nausicaea.excentric.ai.need.Satiation] through [net.minecraft.world.item.ItemStack].
 *
 * [net.minecraft.world.item.ItemStack]s may come from different sources in the game (see [StackSource]) and each source type has a different kind of prerequisite.
 */
data class SatiationItemService(
    val props: FoodProperties,
    val ctx: ItemServiceContext,
) : Service {
    /**
     * [SatiationItemService] is matched with [net.nausicaea.excentric.ai.need.Satiation].
     */
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

    /**
     * Prerequisites depend on the [StackSource].
     */
    override fun prerequisite(): Prerequisite? =
        when (ctx.source) {
            // FIXME: Position isn't the only prerequisite for ForeignInventory
            is StackSource.ForeignInventory -> Position(ctx.source.pos)

            is StackSource.Held -> null

            is StackSource.Inventory -> null

            is StackSource.World -> Position(ctx.source.entity.position())
        }

    /**
     * The base utility is equal to the normalised nutritional value of the food item, as defined in the [FoodProperties] data component of the [net.minecraft.world.item.ItemStack].
     */
    override fun baseUtility(): Probability = of(props.nutrition().toDouble() / MAX_NUTRITION)

    /**
     * Create a [SatiationItemFuture] that promises to fulfil the [SatiationItemService.prerequisite], providing the final benefit to [net.nausicaea.excentric.ai.need.Satiation].
     */
    override fun apply(): Future<Try<Probability>> = SatiationItemFuture(props, ctx, prerequisite())
}

/**
 * If there is no [Prerequisite] or if the prerequisite is satisfied, use the [net.minecraft.world.item.ItemStack] by calling [net.minecraft.world.item.ItemStack.finishUsingItem] and put back the remainder. Then, complete the future with the benefit to the need which is proportional to [FoodProperties.nutrition]. Complete the future with zero benefit if the [net.minecraft.world.item.ItemStack] is empty or doesn't have the [FoodProperties] data component.
 */
class SatiationItemFuture(
    val props: FoodProperties,
    val ctx: ItemServiceContext,
    val prq: Prerequisite?,
    var ready: Boolean = false,
) : Future<Try<Probability>> {
    /**
     * Attempt to bring the [Future] closer to completion.
     *
     * See [SatiationItemFuture] for details
     *
     * @throws IllegalStateException if polled again once [Poll.Ready] has been returned.
     */
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
                prq
                    .poll(ctx.consumer)
                    .inspect { ready = true }
                    .map { Ok(useNow(props, ctx)) }
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
