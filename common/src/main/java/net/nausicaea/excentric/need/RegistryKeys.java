package net.nausicaea.excentric.need;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.ExcentricCommon;

public final class RegistryKeys {
	public static final ResourceKey<Registry<Need>> NEED = ResourceKey.createRegistryKey(ExcentricCommon.id("need"));
}
