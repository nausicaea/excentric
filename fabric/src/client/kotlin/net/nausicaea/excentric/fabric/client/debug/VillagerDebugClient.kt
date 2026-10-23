package net.nausicaea.excentric.fabric.client.debug

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.nausicaea.excentric.fabric.api.network.VillagerDebugPayload

/**
 * Wires up the villager debug overlay: receives the synchronised state and renders it.
 */
object VillagerDebugClient {
    fun register() {
        ClientPlayNetworking.registerGlobalReceiver(VillagerDebugPayload.TYPE) { payload, _ ->
            VillagerDebugStore.update(payload.entries)
        }
        ClientTickEvents.END_CLIENT_TICK.register { VillagerDebugStore.tick() }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> VillagerDebugStore.clear() }
        WorldRenderEvents.AFTER_ENTITIES.register(VillagerDebugRenderer::render)
    }
}
