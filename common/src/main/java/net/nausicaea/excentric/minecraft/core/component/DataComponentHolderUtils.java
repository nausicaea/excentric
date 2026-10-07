package net.nausicaea.excentric.minecraft.core.component;

import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentType;

import java.util.Optional;

public final class DataComponentHolderUtils {
	private DataComponentHolderUtils() {
	}

	public static <T> Optional<T> tryGet(DataComponentHolder self, DataComponentType<? extends T> dataComponentType) {
		return Optional.ofNullable(self.get(dataComponentType));
	}
}
