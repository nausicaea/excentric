package net.nausicaea.excentric.minecraft.world.entity.ai.service;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.java.util.Probability;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Prerequisite;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Need;

import java.util.Optional;

public interface Service {
	ResourceKey<Need> key();
	Prerequisite prerequisite();
	Probability baseUtility();
	Optional<Probability> realise();
}
