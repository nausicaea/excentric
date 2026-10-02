package net.nausicaea.excentric.need;

/// Any item that has an associated probabilistic weight.
public record Weighted<T>(T item, double weight) {
}
