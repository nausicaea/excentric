package net.nausicaea.excentric.fabric.api.lookup

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.service.ItemServiceContext
import net.nausicaea.excentric.ai.service.SatiationItemService
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.ai.service.StackSource
import net.nausicaea.excentric.id
import net.nausicaea.excentric.minecraft.core.component.tryGet
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.map
import net.nausicaea.excentric.util.unwrapOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ServiceApi {
    private val LOG: Logger = LoggerFactory.getLogger(ServiceApi::class.java)
    val ITEM_SERVICE: ItemApiLookup<Service, ItemServiceContext> =
        ItemApiLookup.get(
            id("item_service"),
            Service::class.java,
            ItemServiceContext::class.java,
        )
    val BLOCK_SERVICE: BlockApiLookup<Service, Void> =
        BlockApiLookup.get(id("block_service"), Service::class.java, Void::class.java)
    val ENTITY_SERVICE: EntityApiLookup<Service, Void> =
        EntityApiLookup.get(id("entity_service"), Service::class.java, Void::class.java)

    @JvmStatic
    fun register() {
        LOG.info(LOG_MARKER, "Creating custom Service-API-Lookup APIs")
        // Right now, only item services that offer satiation are supported.
        ITEM_SERVICE.registerFallback { itemStack, ctx ->
            itemStack
                .tryGet(DataComponents.FOOD)
                .map { props -> SatiationItemService(props, ctx) }
                .unwrapOrNull()
        }
        BLOCK_SERVICE.registerFallback { level, blockPos, blockState, blockEntity, ctx ->
            TODO()
        }
        ENTITY_SERVICE.registerFallback { entity, ctx ->
            TODO()
        }
    }

    @JvmStatic
    fun findItemService(
        stack: ItemStack,
        consumer: LivingEntity,
        source: StackSource,
    ): Option<Service> {
        val ctx = ItemServiceContext(consumer, source)
        return tryFindItemApi(ITEM_SERVICE, stack, ctx)
    }

    private fun <A, C> tryFindItemApi(
        api: ItemApiLookup<A, C>,
        itemStack: ItemStack,
        context: C?,
    ): Option<A> = Option.of(api.find(itemStack, context))
}
