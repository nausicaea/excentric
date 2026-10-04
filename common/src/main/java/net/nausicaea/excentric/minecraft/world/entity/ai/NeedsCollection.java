package net.nausicaea.excentric.minecraft.world.entity.ai;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.ExcentricCommon;
import net.nausicaea.excentric.ExcentricRegistries;
import net.nausicaea.excentric.java.util.CollectionUtils;
import net.nausicaea.excentric.java.util.DoubleUtils;
import net.nausicaea.excentric.minecraft.world.entity.ai.decay.DecayFn;
import net.nausicaea.excentric.minecraft.world.entity.ai.need.Need;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;
import net.nausicaea.excentric.minecraft.world.entity.ai.utility.UtilityFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

/// A mutable set of [Need]s that decay over time (see
/// [NeedsCollection#decay(long)]), can be satisfied by [Service]s (see
/// [NeedsCollection#realise(ResourceKey, double)]), and can score [Service]s.
public final class NeedsCollection {
	private static final Logger LOG = LoggerFactory.getLogger(NeedsCollection.class);
	final HashMap<ResourceKey<Need>, Integer> index = new HashMap<>();
	final HashMap<Integer, DecayFn> decayFns = new HashMap<>();
	final HashMap<Integer, UtilityFn> utilityFns = new HashMap<>();
	final HashMap<Integer, Double> satisfactions = new HashMap<>();
	private int maxIndex = 0;
	private long lastUpdateTime = 0L;

	/// Return `true` if the collection contains at least one
	/// [Need], `false` otherwise.
	public boolean isEmpty() {
		return index.isEmpty();
	}

	/// Return the number of [Need]s in the collection.
	public int size() {
		return index.size();
	}

	/// Add a [Need] to the collection. Return `true` if a need was added, `false`
	/// if this need was already present.
	public boolean add(Need need, double initialSatisfaction) {
		return ExcentricRegistries.NEEDS.getResourceKey(need).filter(k -> !this.index.containsKey(k)).map(k -> {
			index.put(k, maxIndex);
			decayFns.put(maxIndex, need.decayFn());
			utilityFns.put(maxIndex, need.utilityFn());
			satisfactions.put(maxIndex, initialSatisfaction);
			maxIndex += 1;
			return true;
		}).orElse(false);
	}

	/// Remove a particular [Need] by its resource key. Return `true` if a need was
	/// removed, `false` otherwise.
	public boolean remove(ResourceKey<Need> needKey) {
		if (!this.index.containsKey(needKey)) {
			return false;
		}
		var idx = this.index.get(needKey);
		this.decayFns.remove(idx);
		this.utilityFns.remove(idx);
		this.satisfactions.remove(idx);
		this.index.remove(needKey);
		return true;
	}

	/// Delete all [Need]s.
	public void clear() {
		this.index.clear();
		this.decayFns.clear();
		this.utilityFns.clear();
		this.satisfactions.clear();
	}

	/// Slowly reduce the [Need]'s satisfaction level through [Need#decayFn()].
	public void decay(long monotonicTime) {
		var deltaTime = (double) Math.max(0L, monotonicTime - lastUpdateTime);
		lastUpdateTime = monotonicTime;
		for (var i : index.values()) {
			satisfactions.compute(i, (k, v) -> {
				if (v == null) {
					return 1.0d;
				} else {
					return DoubleUtils.clamp01(decayFns.get(k).decay(v, deltaTime));
				}
			});
		}

		if (isCritical()) {
			LOG.info(ExcentricCommon.MARKER, "At least one need is in critical condition");
		}
	}

	public boolean isCritical() {
		return satisfactions.values().stream().anyMatch(s -> s < Double.MIN_NORMAL);
	}

	public double urgency() {
		return 1.0d - CollectionUtils.mean(satisfactions.values());
	}

	/// If a [Need] matching the [Service] can be found, calculate a score based on
	/// the need's [UtilityFn]. Otherwise, return `0`. The score is based on the
	/// [Service]'s data, the corresponding [Need]'s initialSatisfaction level, the
	/// overall urgency (i.e. `1 - mean(initialSatisfaction)`), and any
	/// external context.
	public double score(Service svc, NeedsContext context) {
		var i = index.get(svc.key());
		if (i == null) {
			return 0.0d;
		}
		return utilityFns.get(i).score(svc, satisfactions.get(i), context);
	}

	public void realise(ResourceKey<Need> key, double addedIntensity) {
		Optional.ofNullable(index.get(key)).ifPresent(
		    i -> satisfactions.compute(i, (k, v) -> (v == null) ? 1.0d : DoubleUtils.clamp01(addedIntensity + v)));
	}

	public record NeedData(ResourceKey<Need> key, DecayFn decayFn, UtilityFn utilityFn, Double satisfaction) {
	}
}
