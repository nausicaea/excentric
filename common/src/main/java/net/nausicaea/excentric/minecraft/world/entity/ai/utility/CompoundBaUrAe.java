package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.ai.utility.UtilityFnContext;
import net.nausicaea.excentric.java.util.Probability;

/// The utility is the product of the
/// [BaseUtility], [Urgency], and [AerialDistance].
public record CompoundBaUrAe(BaseUtility b, Urgency u, AerialDistance a) implements UtilityFn {
    public CompoundBaUrAe() {
        this(new BaseUtility(), new Urgency(), new AerialDistance());
    }

    @Override
    public Probability score(Service svc, Probability sat, UtilityFnContext ctx) {
        return b.score(svc, sat, ctx).times(u.score(svc, sat, ctx)).times(a.score(svc, sat, ctx));
    }
}
