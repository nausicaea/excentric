package net.nausicaea.excentric.ai.service

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceKey
import net.minecraft.world.food.FoodProperties
import net.nausicaea.excentric.ai.need.Need
import net.nausicaea.excentric.ai.need.SATIATION
import net.nausicaea.excentric.ai.prerequisite.Empty
import net.nausicaea.excentric.ai.prerequisite.Position
import net.nausicaea.excentric.ai.prerequisite.Prerequisite
import net.nausicaea.excentric.minecraft.core.registries.NEED_REGISTRY
import net.nausicaea.excentric.util.Probability
import net.nausicaea.excentric.util.Probability.Companion.of
import java.util.function.Supplier

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

    override fun prerequisite(): Prerequisite =
        when (ctx.source) {
            is Held -> Empty
            is Inventory -> Empty
            is ForeignInventory -> Position(ctx.source.pos)
            is World -> Position(ctx.source.entity.position())
        }

    override fun baseUtility(): Probability = of(props.nutrition().toDouble() / MAX_NUTRITION)

    override fun realise(): Probability {
        val itemStack = ctx.itemStack()
        if (itemStack.isEmpty || !itemStack.has(DataComponents.FOOD)) {
            return Probability.ZERO
        }
        val consumer = ctx.consumer
        val remainder = itemStack.finishUsingItem(consumer.level(), consumer)
        ctx.putBack(remainder)
        return baseUtility()
    }

    companion object {
        const val MAX_NUTRITION: Double = 20.0
    }
}
