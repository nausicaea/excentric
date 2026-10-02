package net.nausicaea.excentric.need;

import net.minecraft.resources.ResourceKey;
import net.nausicaea.excentric.java.util.CollectionUtils;
import net.nausicaea.excentric.java.util.DoubleUtils;

import java.util.HashMap;
import java.util.Optional;

public final class Needs {
	final HashMap<ResourceKey<Need>, Integer> index = new HashMap<>();
	final HashMap<Integer, DecayFn> decayFns = new HashMap<>();
	final HashMap<Integer, ResponseCurveFn> responseCurveFns = new HashMap<>();
	final HashMap<Integer, Double> satisfactions = new HashMap<>();
	private int maxIndex = 0;

	public boolean isEmpty() {
		return index.isEmpty();
	}

	public int size() {
		return index.size();
	}

	public boolean add(Need need) {
		if (this.index.containsKey(need.key())) {
			return false;
		}
		this.index.put(need.key(), maxIndex);
		this.decayFns.put(maxIndex, need.decayFn());
		this.responseCurveFns.put(maxIndex, need.responseCurveFn());
		this.satisfactions.put(maxIndex, need.satisfaction());
		maxIndex += 1;
		return true;
	}

	public boolean remove(ResourceKey<Need> needKey) {
		if (!this.index.containsKey(needKey)) {
			return false;
		}
		var idx = this.index.get(needKey);
		this.decayFns.remove(idx);
		this.responseCurveFns.remove(idx);
		this.satisfactions.remove(idx);
		this.index.remove(needKey);
		return true;
	}

	public void clear() {
		this.index.clear();
		this.decayFns.clear();
		this.responseCurveFns.clear();
		this.satisfactions.clear();
	}

	/// Slowly reduce [Need#satisfaction()] through [Need#decayFn()].
	public void decay(float deltaTime) {
		for (var i : index.values()) {
			satisfactions.compute(i, (k, v) -> (v == null) ? 1.0d : decayFns.get(k).decay(v, deltaTime));
		}
	}

	/// Calculates the total urgency of the collection of [Need]s.
	public double urgency() {
		return 1.0d - CollectionUtils.mean(satisfactions.values());
	}

	public void realise(ResourceKey<Need> key, double addedIntensity) {
		Optional.ofNullable(index.get(key)).ifPresent(
		    i -> satisfactions.compute(i, (k, v) -> (v == null) ? 1.0d : DoubleUtils.clamp01(addedIntensity + v)));
	}

	public record NeedData(ResourceKey<Need> key, DecayFn decayFn, ResponseCurveFn responseCurveFn,
	    Double satisfaction) {
	}
}
