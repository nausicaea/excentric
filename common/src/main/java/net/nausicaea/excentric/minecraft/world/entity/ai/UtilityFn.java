package net.nausicaea.excentric.minecraft.world.entity.ai;

/// Resource curves convert a [Service#baseUtility()] to a subjective utility.
public sealed interface UtilityFn<T> {
	static <U> UtilityFn<U> urgency() {
		return new Urgency<>();
	}

	/// Score a [Service] by the existing [Need] initialSatisfaction value, the
	/// current temperature, and the external context.
	double score(Service svc, double satisfaction, T context);

	record Urgency<T>() implements UtilityFn<T> {
		@Override
		public double score(Service svc, double satisfaction, T context) {
			return (1.0d - satisfaction) * svc.baseUtility();
		}
	}
}
