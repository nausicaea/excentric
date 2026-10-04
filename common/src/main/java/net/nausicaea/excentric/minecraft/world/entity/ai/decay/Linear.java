package net.nausicaea.excentric.minecraft.world.entity.ai.decay;

/// A positive `baseRate` will result in a negative slope for the linear decay.
public record Linear(double baseRate) implements DecayFn {
	@Override
	public double decay(double value, double deltaTime) {
		return value - baseRate * deltaTime;
	}
}
