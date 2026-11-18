package net.nausicaea.excentric.ai.service

import net.minecraft.core.component.DataComponents
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.future.Future
import net.nausicaea.excentric.util.result.Ok
import net.nausicaea.excentric.util.result.Try
import net.nausicaea.excentric.util.task.Pending
import net.nausicaea.excentric.util.task.Poll
import net.nausicaea.excentric.util.task.Ready
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * If there is no [net.nausicaea.excentric.ai.prerequisite.Prerequisite]
 * or if the prerequisite is satisfied, use the [net.minecraft.world.item.ItemStack]
 * by calling [net.minecraft.world.item.ItemStack.finishUsingItem] and put
 * back the remainder. Then, complete the future with the benefit to the need
 * which is proportional to [net.minecraft.world.food.FoodProperties.nutrition].
 * Complete the future with zero benefit if the [net.minecraft.world.item.ItemStack]
 * is empty or doesn't have the [net.minecraft.world.food.FoodProperties] data component.
 */
class FoodItemFuture(
    private var props: FoodProperties?,
    val ctx: ItemServiceContext,
) : Future<Try<Probability>> {
    private fun hasExpired(stack: ItemStack): Boolean =
        (ctx.source is ItemStackSource.World && ctx.source.entity.isRemoved) ||
            stack.isEmpty ||
            !stack.has(
                DataComponents.FOOD,
            )

    private fun useNow(p: FoodProperties): Probability {
        val itemStack = ctx.itemStack()
        if (hasExpired(itemStack)) {
            LOG.info(LOG_MARKER, "Non-food item or item vanished, no benefit")
            return Probability.ZERO
        }
        val consumer = ctx.subject
        val remainder = itemStack.finishUsingItem(consumer.level(), consumer)
        ctx.putBack(remainder)
        val benefit = p.nutrition().toDouble() / MAX_NUTRITION
        LOG.info(LOG_MARKER, "Food-item provides $benefit benefit")
        return Probability.of(benefit)
    }

    override fun poll(): Poll<Try<Probability>> =
        props?.let {
            val result = useNow(it)
            props = null
            Ready(Ok(result))
        } ?: Pending

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(FoodItemFuture::class.java)
    }
}
