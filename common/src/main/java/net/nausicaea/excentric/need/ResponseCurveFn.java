package net.nausicaea.excentric.need;

import net.nausicaea.excentric.java.util.DoubleUtils;

public sealed interface ResponseCurveFn {
	ResponseCurveFn BOLTZMANN = new Boltzmann();

	/// Score a [Service] by the existing [Need] satisfaction value, the current
	/// temperature, and the extended context.
	<T> double score(Service svc, double satisfaction, double temperature, T context);

	record Boltzmann() implements ResponseCurveFn {
		@Override
		public <T> double score(Service svc, double satisfaction, double temperature, T context) {
			var utility = (1.0d - satisfaction) * svc.baseUtility();
			return DoubleUtils.clamp01(Math.expm1(utility / temperature));
		}
	}
}
