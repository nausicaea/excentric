package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public interface Service {
	ResourceKey<Need> key();
	Prerequisite prerequisite();
	double baseUtility();
	Optional<Double> realise();
}
