package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.nausicaea.excentric.minecraft.world.entity.ai.utility.UtilityFnContext;

public interface NeedsContext extends UtilityFnContext {
	NeedsCollection needs();
}
