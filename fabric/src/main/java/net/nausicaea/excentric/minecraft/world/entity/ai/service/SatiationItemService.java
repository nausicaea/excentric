package net.nausicaea.excentric.minecraft.world.entity.ai.service;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.nausicaea.excentric.ai.need.Need;
import net.nausicaea.excentric.ai.need.NeedSingletonsKt;
import net.nausicaea.excentric.ai.prerequisite.Empty;
import net.nausicaea.excentric.ai.prerequisite.Position;
import net.nausicaea.excentric.ai.prerequisite.Prerequisite;
import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.minecraft.core.registries.RegistriesKt;
import net.nausicaea.excentric.util.Probability;
import org.jetbrains.annotations.NotNull;

public record SatiationItemService(FoodProperties props, ItemServiceContext ctx)
        implements Service {
    public static final double MAX_NUTRITION = 20.0d;

    @Override
    public @NotNull ResourceKey<Need> key() {
        return RegistriesKt.NEED_REGISTRY
                .getResourceKey(NeedSingletonsKt.SATIATION)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Cannot find resource key for builtin need 'satiation'"));
    }

    @Override
    public @NotNull Prerequisite prerequisite() {
        return switch (ctx.source()) {
            case StackSource.Held h -> Empty.INSTANCE;
            case StackSource.Inventory i -> Empty.INSTANCE;
            case StackSource.ForeignInventory f -> new Position(f.pos());
            case StackSource.World w -> new Position(w.entity().position());
        };
    }

    @Override
    public @NotNull Probability baseUtility() {
        return Probability.of((double) props.nutrition() / MAX_NUTRITION);
    }

    @Override
    public @NotNull Probability realise() {
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
