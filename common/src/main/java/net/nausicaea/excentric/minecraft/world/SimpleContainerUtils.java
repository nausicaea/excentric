package net.nausicaea.excentric.minecraft.world;

import java.util.stream.Stream;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.nausicaea.excentric.java.util.stream.StreamUtils;

public final class SimpleContainerUtils {
    private SimpleContainerUtils() {}

    public static Stream<StreamUtils.Enumerated<ItemStack>> getItems(SimpleContainer container) {
        return StreamUtils.enumerate(container.getItems().stream())
                .filter(stack -> !stack.value().isEmpty());
    }
}
