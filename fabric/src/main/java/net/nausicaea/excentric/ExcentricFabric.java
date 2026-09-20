package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ExcentricFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ExcentricCommon.onInitialize();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTickCount() % 5 == 0) {
				VillageManager.get(server).debug(server);
			}
			if (server.getTickCount() % VillageManager.TICKS_PER_MAINTENANCE_RUN == 0) {
				VillageManager.get(server).runMaintenance(server);
			}
		});
	}
}
