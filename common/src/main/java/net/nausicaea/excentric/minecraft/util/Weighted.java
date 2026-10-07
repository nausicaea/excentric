package net.nausicaea.excentric.minecraft.util;

import net.nausicaea.excentric.java.util.Probability;

/// Any item that has an associated probabilistic weight.
public record Weighted<T>(T item, Probability weight) {
}
