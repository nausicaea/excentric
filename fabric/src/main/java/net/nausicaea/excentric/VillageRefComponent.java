package net.nausicaea.excentric;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.Optional;
import java.util.UUID;

/// Represents an optional reference to a [Village] by a [java.util.UUID].
public abstract class VillageRefComponent implements Component {
	private static final String TAG = "%svillageId".formatted(ExcentricCommon.MOD_ID);
	private UUID villageId = null;

	/// Getter for an optional [Village] reference.
	public Optional<UUID> villageId() {
		return Optional.ofNullable(this.villageId);
	}

	/// Setter for a [Village] reference.
	public void setVillageId(UUID villageId) {
		if (this.villageId == villageId) {
			return;
		}

		this.villageId = villageId;
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		if (tag.contains(TAG)) {
			villageId = tag.getUUID(TAG);
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		if (villageId == null) {
			return;
		}
		tag.putUUID(TAG, villageId);
	}
}
