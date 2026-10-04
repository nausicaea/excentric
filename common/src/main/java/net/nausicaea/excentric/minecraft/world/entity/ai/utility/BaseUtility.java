package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.java.util.DoubleUtils;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// Calculate the utility as [Service#baseUtility()].
public record BaseUtility() implements UtilityFn {
	@Override
	public double score(Service svc, double satisfaction, UtilityFnContext context) {
		return DoubleUtils.clamp01(svc.baseUtility());
	}
}
