package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.nausicaea.excentric.fabric.api.event.ExcentricEventCallbacks;

public class ExcentricFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ExcentricCommon.onInitialize();
		ExcentricEventCallbacks.register();
	}
}
