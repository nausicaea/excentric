package net.nausicaea.excentric.need;

import net.nausicaea.excentric.java.util.DoubleUtils;

/// Resource curves convert a [Service#baseUtility()] to a value that allows
/// selecting [Service]'s relative to one-another.
public sealed interface ResponseCurveFn {
	ResponseCurveFn BOLTZMANN = new Boltzmann();

	/// Score a [Service] by the existing [Need] satisfaction value, the current
	/// temperature, and the external context.
	<T> double score(Service svc, double satisfaction, double temperature, T context);

	/// Score [Service]s by `exp((1 - satisfaction) * svc_utility / temperature)
	/// - 1`.
	record Boltzmann() implements ResponseCurveFn {
		@Override
		public <T> double score(Service svc, double satisfaction, double temperature, T context) {
			var utility = (1.0d - satisfaction) * svc.baseUtility();
			return DoubleUtils.clamp01(Math.expm1(utility / temperature));
		}
	}
}
