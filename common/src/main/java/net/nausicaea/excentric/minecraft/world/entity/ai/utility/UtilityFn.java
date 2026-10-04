package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.java.util.DoubleUtils;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Prerequisite;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// Resource curves convert a [Service#baseUtility()] to a subjective utility.
public sealed interface UtilityFn {
	/// Score a [Service] by the existing
	/// [net.nausicaea.excentric.minecraft.world.entity.ai.need.Need] initialSatisfaction
	/// value, the current temperature, and the external context.
	double score(Service svc, double satisfaction, UtilityFnContext context);

	record BaseUtility() implements UtilityFn {
		@Override
		public double score(Service svc, double satisfaction, UtilityFnContext context) {
			return DoubleUtils.clamp01(svc.baseUtility());
		}
	}

	record Urgency() implements UtilityFn {
		@Override
		public double score(Service svc, double satisfaction, UtilityFnContext context) {
			return (1.0d - DoubleUtils.clamp01(satisfaction));
		}
	}

	record AerialDistance() implements UtilityFn {
		@Override
		public double score(Service svc, double satisfaction, UtilityFnContext context) {
			if (!(svc.prerequisite() instanceof Prerequisite.Position(Vec3 pos))) {
				return 1.0d;
			}
			return 1.0d / Math.max(1.0d, Math.abs(context.entity().position().distanceTo(pos)));
		}
	}

	record Example(BaseUtility b, Urgency u, AerialDistance a) implements UtilityFn {
		public Example() {
			this(new BaseUtility(), new Urgency(), new AerialDistance());
		}

		@Override
		public double score(Service svc, double sat, UtilityFnContext ctx) {
			return b.score(svc, sat, ctx) * u.score(svc, sat, ctx) * a.score(svc, sat, ctx);
		}
	}
}
