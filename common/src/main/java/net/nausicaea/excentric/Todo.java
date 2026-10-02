package net.nausicaea.excentric;

/// Flag a yet unimplemented piece of code as implementation planned.
public class Todo extends RuntimeException {
	public Todo() {
		super("not yet implemented");
	}

	public Todo(String message) {
		super("not yet implemented: %s".formatted(message));
	}
}
