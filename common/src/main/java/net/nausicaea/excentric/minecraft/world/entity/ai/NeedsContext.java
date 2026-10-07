package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;
import net.nausicaea.excentric.minecraft.world.entity.ai.utility.UtilityFnContext;

public interface NeedsContext extends ReasonerContext<Service>, UtilityFnContext {
	NeedsCollection needs();
}
