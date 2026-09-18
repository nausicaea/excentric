package net.nausicaea.excentric;

import java.util.Optional;
import java.util.UUID;

/// Represents an optional reference to a [Village] by a [java.util.UUID].
public interface VillageRef {
	/// Getter for an optional [Village] reference.
	default Optional<UUID> villageMod$getVillageId() {
		throw new AssertionError("Implemented in Mixin");
	}

	/// Setter for a [Village] reference.
	default void villageMod$setVillageId(UUID id) {
		throw new AssertionError("Implemented in Mixin");
	}
}
