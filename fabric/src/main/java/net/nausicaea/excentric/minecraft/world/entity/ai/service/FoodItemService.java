package net.nausicaea.excentric.minecraft.world.entity.ai.service;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.nausicaea.excentric.ExcentricRegistries;
import net.nausicaea.excentric.java.util.Probability;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Need;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Needs;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Empty;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Position;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Prerequisite;

public record FoodItemService(FoodProperties props, ItemServiceContext ctx) implements Service {
	public static final double MAX_NUTRITION = 20.0d;

	@Override
	public ResourceKey<Need> key() {
		return ExcentricRegistries.NEEDS.getResourceKey(Needs.SATIATION)
		    .orElseThrow(() -> new RuntimeException("Cannot find resource key for builtin need 'satiation'"));
	}

	@Override
	public Prerequisite prerequisite() {
		return switch (ctx.source()) {
			case StackSource.Held h -> new Empty();
			case StackSource.Inventory i -> new Empty();
			case StackSource.ForeignInventory f -> new Position(f.pos());
			case StackSource.World w -> new Position(w.entity().position());
		};
	}

	@Override
	public Probability baseUtility() {
		return Probability.of((double) props.nutrition() / MAX_NUTRITION);
	}

	@Override
	public Probability realise() {
		var itemStack = ctx.itemStack();
		if (itemStack.isEmpty() || !itemStack.has(DataComponents.FOOD)) {
			return Probability.ZERO;
		}
		var consumer = ctx.consumer();
		var remainder = itemStack.finishUsingItem(consumer.level(), consumer);
		ctx.putBack(remainder);
		return baseUtility();
	}
}
