package net.nausicaea.excentric;

import org.apache.commons.lang3.NotImplementedException;

/// Flag a yet unimplemented piece of code as implementation planned.
public class Todo extends NotImplementedException {
	public Todo() {
		super("not yet implemented");
	}

	public Todo(String message) {
		super("not yet implemented: %s".formatted(message));
	}
}
