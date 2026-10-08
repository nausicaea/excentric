package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.java.util.Probability;

/// Calculate the utility as [Service#baseUtility()].
public record BaseUtility() implements UtilityFn {
    @Override
    public Probability score(Service svc, Probability satisfaction, UtilityFnContext context) {
        return svc.baseUtility();
    }
}
