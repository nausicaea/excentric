package net.nausicaea.excentric;

import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.FoodItemService;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.ItemServiceContext;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.StackSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static net.nausicaea.excentric.minecraft.core.component.DataComponentHolderUtils.tryGet;

public final class ExcentricServiceApi {
	private static final Logger LOG = LoggerFactory.getLogger(ExcentricServiceApi.class);
	public static final ItemApiLookup<Service, ItemServiceContext> ITEM_SERVICE = ItemApiLookup
	    .get(ExcentricCommon.id("item_service"), Service.class, ItemServiceContext.class);

	public static void register() {
		LOG.info(ExcentricCommon.MARKER, "Creating custom Service-API-Lookup APIs");
		ITEM_SERVICE.registerFallback((itemStack, ctx) -> tryGet(itemStack, DataComponents.FOOD)
		    .map(props -> new FoodItemService(props, ctx)).orElse(null));
	}

	public static Optional<Service> findItemService(ItemStack stack, LivingEntity consumer, StackSource source) {
		var ctx = new ItemServiceContext(consumer, source);
		return tryFindItemApi(ITEM_SERVICE, stack, ctx);
	}

	private static <A, C> Optional<A> tryFindItemApi(ItemApiLookup<A, C> api, ItemStack itemStack, C context) {
		return Optional.ofNullable(api.find(itemStack, context));
	}
}
