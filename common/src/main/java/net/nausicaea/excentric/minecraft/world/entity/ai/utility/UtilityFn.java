package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// Resource curves convert a [Service#baseUtility()] to a subjective utility.
public interface UtilityFn {
	/// Score a [Service] by the existing
	/// [net.nausicaea.excentric.minecraft.world.entity.ai.need.Need] initialSatisfaction
	/// value, the current temperature, and the external context.
	double score(Service svc, double satisfaction, UtilityFnContext context);
}
