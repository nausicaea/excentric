package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// The utility is the product of the
/// [BaseUtility], [Urgency], and [AerialDistance].
public record Example(BaseUtility b, Urgency u, AerialDistance a) implements UtilityFn {
	public Example() {
		this(new BaseUtility(), new Urgency(), new AerialDistance());
	}

	@Override
	public double score(Service svc, double sat, UtilityFnContext ctx) {
		return b.score(svc, sat, ctx) * u.score(svc, sat, ctx) * a.score(svc, sat, ctx);
	}
}
