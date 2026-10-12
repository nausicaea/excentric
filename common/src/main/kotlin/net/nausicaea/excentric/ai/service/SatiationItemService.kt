package net.nausicaea.excentric.ai.service

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceKey
import net.minecraft.world.food.FoodProperties
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.SATIATION
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Future
import net.nausicaea.excentric.util.Poll
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.of
import net.nausicaea.excentric.util.Try
import java.util.function.Supplier

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
            is StackSource.ForeignInventory -> Position(ctx.source.pos)
            is StackSource.Held -> null
            is StackSource.Inventory -> null
            is StackSource.World -> Position(ctx.source.entity.position())
        }

    override fun baseUtility(): Probability = of(props.nutrition().toDouble() / MAX_NUTRITION)

    override fun use(): Future<Try<Probability>> {
        return SatiationItemFuture(props, ctx, prerequisite())
    }
}

data class SatiationItemFuture(val props: FoodProperties, val ctx: ItemServiceContext, val prq: Prerequisite?) :
    Future<Try<Probability>> {
    override fun poll(): Poll<Try<Probability>> {
        val prq = prq ?: return Poll.Ready(Try.Ok(useNow(props, ctx)))
        TODO()
    }

    private fun useNow(props: FoodProperties, ctx: ItemServiceContext): Probability {
        val itemStack = ctx.itemStack()
        if (itemStack.isEmpty || !itemStack.has(DataComponents.FOOD)) {
            return Probability.ZERO
        }
        val consumer = ctx.consumer
        val remainder = itemStack.finishUsingItem(consumer.level(), consumer)
        ctx.putBack(remainder)
        return of(props.nutrition().toDouble() / MAX_NUTRITION)
    }
}
