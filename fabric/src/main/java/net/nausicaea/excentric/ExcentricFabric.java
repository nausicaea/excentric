package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;

public class ExcentricFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerAccess.register();
		ExcentricCommon.onInitialize();

		ServerChunkEvents.CHUNK_LOAD.register(ExcentricEventCallbacks::onServerChunkLoad);
		// TODO: add ServerBlockEntityEvents.BLOCK_ENTITY_LOAD
		// TODO: add ServerEntityEvents.ENTITY_LOAD
		ExcentricEvents.AFTER_BLOCK_PLACE.register(ExcentricEventCallbacks::onAfterBlockPlace);
	}
}
