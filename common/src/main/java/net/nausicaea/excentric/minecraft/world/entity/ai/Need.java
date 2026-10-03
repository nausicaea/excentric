package net.nausicaea.excentric.minecraft.world.entity.ai;

public interface Need {
	DecayFn decayFn();
	UtilityFn<NeedsContext> utilityFn();
}
