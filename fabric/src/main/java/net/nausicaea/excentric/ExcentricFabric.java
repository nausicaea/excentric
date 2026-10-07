package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.nausicaea.excentric.fabric.api.event.EventCallbacks;

public class ExcentricFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ExcentricCommon.onInitialize();
		EventCallbacks.register();
		ExcentricServiceApi.register();
	}
}
