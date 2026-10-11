package net.nausicaea.excentric;

import java.util.Optional;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.nausicaea.excentric.ai.service.ItemServiceContext;
import net.nausicaea.excentric.ai.service.SatiationItemService;
import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.ai.service.StackSource;
import net.nausicaea.excentric.minecraft.core.component.DataComponentHolderExtKt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExcentricServiceApi {
    private static final Logger LOG = LoggerFactory.getLogger(ExcentricServiceApi.class);
    public static final ItemApiLookup<Service, ItemServiceContext> ITEM_SERVICE =
            ItemApiLookup.get(CommonKt.id("item_service"), Service.class, ItemServiceContext.class);
    public static final BlockApiLookup<Service, Void> BLOCK_SERVICE =
            BlockApiLookup.get(CommonKt.id("block_service"), Service.class, Void.class);
    public static final EntityApiLookup<Service, Void> ENTITY_SERVICE =
            EntityApiLookup.get(CommonKt.id("entity_service"), Service.class, Void.class);

    public static void register() {
        LOG.info(CommonKt.LOG_MARKER, "Creating custom Service-API-Lookup APIs");
        // Right now, only item services that offer satiation are supported.
        ITEM_SERVICE.registerFallback(
                (itemStack, ctx) ->
                        DataComponentHolderExtKt.tryGet(itemStack, DataComponents.FOOD)
                                .toOptional()
                                .map(props -> new SatiationItemService(props, ctx))
                                .orElse(null));
        BLOCK_SERVICE.registerFallback(
                (level, blockPos, blockState, blockEntity, ctx) -> {
                    throw new Todo();
                });
        ENTITY_SERVICE.registerFallback(
                (entity, ctx) -> {
                    throw new Todo();
                });
    }

    public static Optional<Service> findItemService(
            ItemStack stack, LivingEntity consumer, StackSource source) {
        var ctx = new ItemServiceContext(consumer, source);
        return tryFindItemApi(ITEM_SERVICE, stack, ctx);
    }

    private static <A, C> Optional<A> tryFindItemApi(
            ItemApiLookup<A, C> api, ItemStack itemStack, C context) {
        return Optional.ofNullable(api.find(itemStack, context));
    }
}
