package net.nausicaea.excentric.need;

import net.minecraft.resources.ResourceKey;

import java.util.Optional;

public interface Service {
	ResourceKey<Need> key();
	Prerequisite prerequisite();
	double baseUtility();
	Optional<Double> realise();
}
