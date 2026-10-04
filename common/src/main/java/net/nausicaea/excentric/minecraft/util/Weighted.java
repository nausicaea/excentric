package net.nausicaea.excentric.minecraft.util;

/// Any item that has an associated probabilistic weight.
public record Weighted<T>(T item, double weight) {
}
