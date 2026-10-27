package net.nausicaea.excentric.fabric.api.lookup

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BedBlockEntity
import net.nausicaea.excentric.LOG_MARKER
import net.nausicaea.excentric.ai.service.BedBlockService
import net.nausicaea.excentric.ai.service.BlockServiceContext
import net.nausicaea.excentric.ai.service.EntityServiceContext
import net.nausicaea.excentric.ai.service.FoodItemService
import net.nausicaea.excentric.ai.service.ItemServiceContext
import net.nausicaea.excentric.ai.service.ItemStackSource
import net.nausicaea.excentric.ai.service.Service
import net.nausicaea.excentric.id
import net.nausicaea.excentric.util.option.asOpt
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
    val BLOCK_SERVICE: BlockApiLookup<Service, BlockServiceContext> =
        BlockApiLookup.get(id("block_service"), Service::class.java, BlockServiceContext::class.java)
    val ENTITY_SERVICE: EntityApiLookup<Service, EntityServiceContext> =
        EntityApiLookup.get(id("entity_service"), Service::class.java, EntityServiceContext::class.java)

    /**
     * Register Fabric's API-Lookup-API fallbacks for [ITEM_SERVICE], [BLOCK_SERVICE], and [ENTITY_SERVICE].
     */
    @JvmStatic
    fun register() {
        LOG.info(LOG_MARKER, "Creating custom Service-API-Lookup APIs")
        // Right now, only item services that offer satiation are supported.
        ITEM_SERVICE.registerFallback { itemStack, ctx ->
            itemStack
                .get(DataComponents.FOOD)
                .asOpt()
                .map { FoodItemService(it, ctx) }
                .unwrapOrNull
        }
        BLOCK_SERVICE.registerFallback { level, blockPos, blockState, blockEntity, ctx ->
            blockEntity
                .asOpt()
                .filter { it is BedBlockEntity }
                .map { BedBlockService(blockPos, ctx) }
                .unwrapOrNull
        }
        /* TODO: Implement ENTITY_SERVICE fallbacks
        ENTITY_SERVICE.registerFallback { entity, ctx ->
            null
        }
         */
    }

    /**
     * Helper method to find a [Service] from an [ItemStack].
     *
     * @param stack the stack of [net.minecraft.world.item.Item]s that maybe has an attached [Service].
     * @param consumer the entity that consumes the [ItemStack].
     * @param source where the [ItemStack] comes from (see [ItemStackSource] for details).
     */
    @JvmStatic
    fun findItemService(
        stack: ItemStack,
        consumer: LivingEntity,
        source: ItemStackSource,
    ): Service? = ITEM_SERVICE.find(stack, ItemServiceContext(consumer, source))
}
