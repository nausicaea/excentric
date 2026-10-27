package net.nausicaea.excentric.ai.service

import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.SATIATION
import net.nausicaea.excentric.ai.prerequisite.Fulfilled
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.of
import net.nausicaea.excentric.util.Try
import net.nausicaea.excentric.util.future.Future
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private const val MAX_NUTRITION: Double = 20.0

/**
 * Defines the a [Service] for [net.nausicaea.excentric.ai.need.Satiation] through [net.minecraft.world.item.ItemStack].
 *
 * [net.minecraft.world.item.ItemStack]s may come from different sources in the game (see [ItemStackSource]) and each source type has a different kind of prerequisite.
 */
data class FoodItemService(
    val props: FoodProperties,
    val ctx: ItemServiceContext,
) : Service {
    /**
     * [FoodItemService] is matched with [net.nausicaea.excentric.ai.need.Satiation].
     */
    override fun key(): ResourceKey<Need> =
        NEED_REGISTRY
            .getResourceKey(SATIATION)
            .orElseThrow {
                IllegalStateException(
                    "Cannot find resource key for builtin need 'satiation'",
                )
            }

    /**
     * Prerequisites depend on the [ItemStackSource].
     */
    override fun prerequisite(): Prerequisite =
        when (ctx.source) {
            // FIXME: Position isn't the only prerequisite for ForeignInventory
            is ItemStackSource.ForeignInventory -> Position(ctx.source.pos)

            is ItemStackSource.Held -> Fulfilled

            is ItemStackSource.Inventory -> Fulfilled

            is ItemStackSource.World -> Position(ctx.source.entity.position())
        }

    /**
     * The base utility is equal to the normalised nutritional value of the food item, as defined in the [FoodProperties] data component of the [net.minecraft.world.item.ItemStack].
     */
    override fun baseUtility(): Probability = of(props.nutrition().toDouble() / MAX_NUTRITION)

    override fun describe(): String {
        val stack = ctx.itemStack()
        val source = ctx.source::class.simpleName
        if (stack.isEmpty) {
            return "food item (gone) from $source"
        }
        return "${BuiltInRegistries.ITEM.getKey(stack.item)} x${stack.count} from $source"
    }

    /**
     * Create a [FoodItemFuture] that promises to fulfil the [FoodItemService.prerequisite], providing the final benefit to [net.nausicaea.excentric.ai.need.Satiation].
     */
    override fun apply(): Future<Try<Probability>> = FoodItemFuture(props, ctx, prerequisite().fulfil(ctx.consumer))

    /**
     * If there is no [Prerequisite] or if the prerequisite is satisfied, use the [net.minecraft.world.item.ItemStack] by calling [net.minecraft.world.item.ItemStack.finishUsingItem] and put back the remainder. Then, complete the future with the benefit to the need which is proportional to [FoodProperties.nutrition]. Complete the future with zero benefit if the [net.minecraft.world.item.ItemStack] is empty or doesn't have the [FoodProperties] data component.
     */
    class FoodItemFuture(
        val props: FoodProperties,
        val ctx: ItemServiceContext,
        prq: Future<Try<Unit>>,
    ) : ServiceFuture(prq) {
        private fun hasExpired(stack: ItemStack): Boolean =
            (ctx.source is ItemStackSource.World && ctx.source.entity.isRemoved) || stack.isEmpty || !stack.has(DataComponents.FOOD)

        override fun hasExpired(): Boolean = hasExpired(ctx.itemStack())

        override fun useNow(): Probability {
            val itemStack = ctx.itemStack()
            if (hasExpired(itemStack)) {
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
            private val LOG: Logger = LoggerFactory.getLogger(FoodItemFuture::class.java)
        }
    }
}
