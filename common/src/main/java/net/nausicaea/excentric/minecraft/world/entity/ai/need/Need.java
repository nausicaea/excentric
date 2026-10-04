package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.nausicaea.excentric.minecraft.world.entity.ai.decay.DecayFn;
import net.nausicaea.excentric.minecraft.world.entity.ai.utility.UtilityFn;

public interface Need {
	DecayFn decayFn();
	UtilityFn utilityFn();
}
