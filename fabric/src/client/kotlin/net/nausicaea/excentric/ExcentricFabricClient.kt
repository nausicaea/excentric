package net.nausicaea.excentric

import net.fabricmc.api.ClientModInitializer
import net.nausicaea.excentric.fabric.client.debug.VillagerDebugClient

/**
 * Client entrypoint. Client code is used for debugging only (see ADR 001 and ADR 003).
 */
class ExcentricFabricClient : ClientModInitializer {
    override fun onInitializeClient() {
        VillagerDebugClient.register()
    }
}
