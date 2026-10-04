package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.java.util.Probability;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// Calculate the utility as [Service#baseUtility()].
public record BaseUtility() implements UtilityFn {
	@Override
	public Probability score(Service svc, Probability satisfaction, UtilityFnContext context) {
		return svc.baseUtility();
	}
}
