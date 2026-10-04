package net.nausicaea.excentric.minecraft.world.entity.ai.utility;

import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Position;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

/// Calculate the utility as the multiplicative inverse (`1/x`) of the
/// [net.minecraft.world.entity.Entity] distance to the service (as the
/// crow flies).
public record AerialDistance() implements UtilityFn {
	@Override
	public double score(Service svc, double satisfaction, UtilityFnContext context) {
		if (!(svc.prerequisite() instanceof Position(Vec3 pos))) {
			return 1.0d;
		}
		return 1.0d / Math.max(1.0d, Math.abs(context.entity().position().distanceTo(pos)));
	}
}
