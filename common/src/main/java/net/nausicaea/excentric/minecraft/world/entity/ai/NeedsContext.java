package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.nausicaea.excentric.ai.ReasonerContext;
import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.ai.utility.UtilityFnContext;

public interface NeedsContext extends ReasonerContext<Service>, UtilityFnContext {
    NeedsCollection needs();
}
