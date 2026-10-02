package net.nausicaea.excentric.need;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.java.util.CollectionUtils;
import net.nausicaea.excentric.java.util.DoubleUtils;

import java.util.HashMap;
import java.util.Optional;

/// A mutable set of [Need]s that decay over time (see [Needs#decay(long)]), can
/// be satisfied by [Service]s (see [Needs#realise(ResourceKey, double)]), and
/// can score [Service]s.
public final class Needs {
	final HashMap<ResourceKey<Need>, Integer> index = new HashMap<>();
	final HashMap<Integer, DecayFn> decayFns = new HashMap<>();
	final HashMap<Integer, ResponseCurveFn> responseCurveFns = new HashMap<>();
	final HashMap<Integer, Double> satisfactions = new HashMap<>();
	private double urgency = 0.0d;
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
	public boolean add(Need need) {
		if (this.index.containsKey(need.key())) {
			return false;
		}
		this.index.put(need.key(), maxIndex);
		this.decayFns.put(maxIndex, need.decayFn());
		this.responseCurveFns.put(maxIndex, need.responseCurveFn());
		this.satisfactions.put(maxIndex, need.satisfaction());
		maxIndex += 1;
		updateUrgency();
		return true;
	}

	/// Remove a particular [Need] by its resource key. Return `true` if a need was
	/// removed, `false` otherwise.
	public boolean remove(ResourceKey<Need> needKey) {
		if (!this.index.containsKey(needKey)) {
			return false;
		}
		var idx = this.index.get(needKey);
		this.decayFns.remove(idx);
		this.responseCurveFns.remove(idx);
		this.satisfactions.remove(idx);
		this.index.remove(needKey);
		updateUrgency();
		return true;
	}

	/// Delete all [Need]s.
	public void clear() {
		this.index.clear();
		this.decayFns.clear();
		this.responseCurveFns.clear();
		this.satisfactions.clear();
		updateUrgency();
	}

	/// Slowly reduce [Need#satisfaction()] through [Need#decayFn()].
	public void decay(long monotonicTime) {
		var deltaTime = (double) Math.max(0L, monotonicTime - lastUpdateTime);
		lastUpdateTime = monotonicTime;
		for (var i : index.values()) {
			satisfactions.compute(i, (k, v) -> (v == null) ? 1.0d : decayFns.get(k).decay(v, deltaTime));
		}
		updateUrgency();
	}

	/// Calculate the total urgency of the collection of [Need]s.
	private void updateUrgency() {
		urgency = 1.0d - CollectionUtils.mean(satisfactions.values());
	}

	/// If a [Need] matching the [Service] can be found, calculate a score based on
	/// the need's [ResponseCurveFn]. Otherwise, return `0`. The score is based on
	/// the [Service]'s data, the corresponding [Need]'s satisfaction level, the
	/// overall urgency (i.e. `1 - mean(satisfaction)`), and any external context.
	public <T> double score(Service svc, T context) {
		var i = index.get(svc.key());
		if (i == null) {
			return 0.0d;
		}
		return responseCurveFns.get(i).score(svc, satisfactions.get(i), urgency, context);
	}

	public void realise(ResourceKey<Need> key, double addedIntensity) {
		Optional.ofNullable(index.get(key)).ifPresent(
		    i -> satisfactions.compute(i, (k, v) -> (v == null) ? 1.0d : DoubleUtils.clamp01(addedIntensity + v)));
		updateUrgency();
	}

	public record NeedData(ResourceKey<Need> key, DecayFn decayFn, ResponseCurveFn responseCurveFn,
	    Double satisfaction) {
	}
}
