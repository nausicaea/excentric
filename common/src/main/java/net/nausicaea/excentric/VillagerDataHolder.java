package net.nausicaea.excentric;

public interface VillagerDataHolder {
	default VillagerData villageMod$getVillagerData() {
		throw new AssertionError("Implemented in Mixin");
	}

	default void villageMod$setVillagerData(VillagerData data) {
		throw new AssertionError("Implemented in Mixin");
	}
}
