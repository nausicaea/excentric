package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

public class ExcentricFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		ServerAccess.register();
		ExcentricCommon.onInitialize();

		ServerChunkEvents.CHUNK_LOAD.register(ExcentricEventCallbacks::onChunkLoad);
		ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(ExcentricEventCallbacks::onBlockEntityLoad);
		ServerEntityEvents.ENTITY_LOAD.register(ExcentricEventCallbacks::onEntityLoad);
		ExcentricEvents.AFTER_BLOCK_PLACE.register(ExcentricEventCallbacks::onAfterBlockPlace);
	}
}
