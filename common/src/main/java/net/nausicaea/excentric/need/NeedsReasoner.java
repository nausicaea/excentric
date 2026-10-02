package net.nausicaea.excentric.need;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.nausicaea.excentric.Todo;

import java.util.*;
import java.util.stream.Stream;

public final class NeedsReasoner implements Reasoner<Service, NeedsContext> {
	private final Needs needs;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<Service> currentService;

	public NeedsReasoner(Needs needs) {
		this.needs = needs;
		this.currentService = Optional.empty();
	}

	@Override
	public void setAction(Service action) {
		this.currentService = Optional.of(action);
	}

	/// Collect services from various sources.
	@Override
	public Stream<Service> collect(NeedsContext context) {
		throw new Todo();
	}

	/// Score each service with [Need#responseCurveFn()].
	@Override
	public Stream<Weighted<Service>> score(Stream<Service> services, NeedsContext context) {
		var temp = needs.urgency();
		return services.flatMap(svc -> Optional.ofNullable(needs.index.get(svc.key())).map(i -> new Weighted<>(svc,
		    needs.responseCurveFns.get(i).score(svc, needs.satisfactions.get(i), temp, context))).stream());
	}

	/// Randomly select from the available [Service]s by their score.
	@Override
	public Optional<Service> select(Stream<Weighted<Service>> rankedServices, RandomSource rng) {
		return RandomSourceUtils.chooseWeighted(rng, rankedServices.toList());
	}

	/// If a [Service] is currently selected as a target, try to
	/// [Service#realise()] the [Service] against the corresponding [Need]. In other
	/// words, try to satisfy the matching [Need] with the current [Service]. This
	/// guarantees to call [Prerequisite#poll()] exactly once. Call
	/// [Service#realise()] only if the prerequisite is satisfied (e.g. the return
	/// value from [Prerequisite#poll()] is equal to [Prerequisite.State#SATISFIED]).
	public void tryRealiseService() {
		currentService.map(SvcPrq::new).map(SvcPrq::poll).flatMap(SvcPolled::tryRealise)
		    .ifPresent(t -> needs.realise(t.key, t.addedAmount));
	}

	/// Helper throwaway `record` that aims to make
	/// [NeedsReasoner#tryRealiseService()] more easily readable.
	private record SvcPrq(Service svc, Prerequisite prq) {
		SvcPrq(Service svc) {
			this(svc, svc.prerequisite());
		}

		/// Poll the [Prerequisite] once.
		SvcPolled poll() {
			return new SvcPolled(svc, prq.poll());
		}
	}

	/// Helper throwaway `record` that aims to make
	/// [NeedsReasoner#tryRealiseService()] more easily readable.
	private record SvcPolled(Service svc, Prerequisite.State state) {
		/// Try to call [Service#realise()] if the [Prerequisite.State] is equal to
		/// [Prerequisite.State#SATISFIED]. Otherwise, return [Optional#empty()].
		Optional<SvcRealised> tryRealise() {
			if (state != Prerequisite.State.SATISFIED) {
				return Optional.empty();
			}

			var svcKey = svc.key();
			return svc.realise().map(amt -> new SvcRealised(svcKey, amt));
		}
	}

	/// Helper throwaway `record` that aims to make
	/// [NeedsReasoner#tryRealiseService()] more easily readable.
	private record SvcRealised(ResourceKey<Need> key, double addedAmount) {
	}
}
