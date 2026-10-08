package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.ai.utility.UtilityFnContext;
import net.nausicaea.excentric.java.util.Probability;

/// Resource curves convert a [Service#baseUtility()] to a subjective utility.
public interface UtilityFn {
    /// Score a [Service] by the existing
    /// [net.nausicaea.excentric.minecraft.world.entity.ai.need.Need] initialSatisfaction
    /// value, the current temperature, and the external context.
    ///
    /// The return value is guaranteed to be normalised to [0..1].
    Probability score(Service svc, Probability satisfaction, UtilityFnContext context);
}
