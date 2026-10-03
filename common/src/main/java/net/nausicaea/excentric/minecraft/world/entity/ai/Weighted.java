package net.nausicaea.excentric.minecraft.world.entity.ai;

/// Any item that has an associated probabilistic weight.
public record Weighted<T>(T item, double weight) {
}
