package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.ExcentricCommon;

public final class RegistryKeys {
	public static final ResourceKey<Registry<Need>> NEED = ResourceKey.createRegistryKey(ExcentricCommon.id("need"));
}
