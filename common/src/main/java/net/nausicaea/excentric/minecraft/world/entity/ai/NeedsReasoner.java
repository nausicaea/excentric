package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.nausicaea.excentric.Todo;
import net.nausicaea.excentric.java.util.Probability;
import net.nausicaea.excentric.minecraft.util.RandomSourceUtils;
import net.nausicaea.excentric.minecraft.util.Weighted;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Need;
import net.nausicaea.excentric.minecraft.world.entity.ai.prerequisite.Prerequisite;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;

import java.util.*;
import java.util.stream.Stream;

public final class NeedsReasoner implements Reasoner<Service, NeedsContext> {
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<SvcPrq> currentService;

	public NeedsReasoner() {
		this.currentService = Optional.empty();
	}

	@Override
	public void setAction(Service action) {
		this.currentService = Optional.of(new SvcPrq(action));
	}

	/// Collect services from various sources.
	@Override
	public Stream<Service> collect(NeedsContext context) {
		throw new Todo();
	}

	/// For every service, call [NeedsCollection#score(Service, NeedsContext)]
	@Override
	public Stream<Weighted<Service>> score(Stream<Service> services, NeedsContext context) {
		var needs = context.needs();
		var temp = needs.urgency();
		return services.map(svc -> new Weighted<>(svc, Probability.boltzmann(needs.score(svc, context), temp)));
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
	/// value from [Prerequisite#poll()] is equal to [Prerequisite.State.Satisfied]).
	public void tryRealiseService(NeedsContext context) {
		currentService.map(SvcPrq::poll).flatMap(SvcPolled::tryRealise)
		    .ifPresent(t -> context.needs().realise(t.key, t.addedAmount));
	}

	/// Helper throwaway `record` that aims to make
	/// [NeedsReasoner#tryRealiseService(NeedsContext context)] more easily readable.
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
	/// [NeedsReasoner#tryRealiseService(NeedsContext context)] more easily readable.
	private record SvcPolled(Service svc, Prerequisite.State state) {
		/// Try to call [Service#realise()] if the [Prerequisite.State] is equal to
		/// [Prerequisite.State.Satisfied]. Otherwise, return [Optional#empty()].
		Optional<SvcRealised> tryRealise() {
			if (!(state instanceof Prerequisite.State.Satisfied)) {
				return Optional.empty();
			}

			var svcKey = svc.key();
			return Optional.of(new SvcRealised(svcKey, svc.realise()));
		}
	}

	/// Helper throwaway `record` that aims to make
	/// [NeedsReasoner#tryRealiseService(NeedsContext context)] more easily readable.
	private record SvcRealised(ResourceKey<Need> key, Probability addedAmount) {
	}
}
