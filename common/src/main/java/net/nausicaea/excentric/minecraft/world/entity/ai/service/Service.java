package net.nausicaea.excentric.minecraft.world.entity.ai.service;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Prerequisite;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Need;

import java.util.Optional;

public interface Service {
	ResourceKey<Need> key();
	Prerequisite prerequisite();
	double baseUtility();
	Optional<Double> realise();
}
