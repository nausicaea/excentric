package net.nausicaea.excentric.minecraft.world.entity.ai.need;

import net.nausicaea.excentric.minecraft.world.entity.ai.DecayFn;
import net.nausicaea.excentric.minecraft.world.entity.ai.NeedsContext;
import net.nausicaea.excentric.minecraft.world.entity.ai.UtilityFn;

public interface Need {
	DecayFn decayFn();
	UtilityFn<NeedsContext> utilityFn();
}
