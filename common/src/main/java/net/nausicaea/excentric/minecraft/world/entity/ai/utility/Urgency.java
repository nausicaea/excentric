package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.nausicaea.excentric.java.util.DoubleUtils;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// Calculate the utility as the inverse probability of the need's satisfaction.
public record Urgency() implements UtilityFn {
	@Override
	public double score(Service svc, double satisfaction, UtilityFnContext context) {
		return (1.0d - DoubleUtils.clamp01(satisfaction));
	}
}
