package net.nausicaea.excentric.fabric.api.network

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.npc.Villager
import net.nausicaea.excentric.ai.debug.NeedsDebugSource
import net.nausicaea.excentric.util.EveryN
import java.util.UUID

/**
 * Sends [VillagerDebugPayload]s every [INTERVAL] ticks to those operators that have opted in to the villager
 * debug overlay (see [net.nausicaea.excentric.fabric.api.command.ExcentricCommands]).
 *
 * The opt-in state is deliberately not persisted: it is cleared when the player disconnects or the server stops.
 */
object VillagerDebugBroadcaster {
    private const val INTERVAL = 8
    private const val RADIUS = 32.0
    private const val OPERATOR_LEVEL = 2
    private val subscribers: MutableSet<UUID> = HashSet()
    private val everyInterval = EveryN(INTERVAL)

    @JvmStatic
    fun register() {
        PayloadTypeRegistry.playS2C().register(VillagerDebugPayload.TYPE, VillagerDebugPayload.CODEC)
        ServerTickEvents.END_SERVER_TICK.register(::onEndTick)
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ -> subscribers.remove(handler.player.uuid) }
    }

    fun isEnabled(player: ServerPlayer): Boolean = player.uuid in subscribers

    /**
     * Opt the player in or out. Return `true` if the state was applied. Opting in fails if the player's client cannot
     * receive the payload (i.e. the mod is not installed on the client).
     */
    fun setEnabled(
        player: ServerPlayer,
        enabled: Boolean,
    ): Boolean {
        val canSend = ServerPlayNetworking.canSend(player, VillagerDebugPayload.TYPE)
        if (enabled) {
            if (!canSend) {
                return false
            }
            subscribers.add(player.uuid)
        } else {
            subscribers.remove(player.uuid)
            if (canSend) {
                ServerPlayNetworking.send(player, VillagerDebugPayload(emptyList()))
            }
        }
        return true
    }

    private fun onEndTick(server: MinecraftServer) {
        if (subscribers.isEmpty()) {
            return
        }
        everyInterval.run { broadcast(server) }
    }

    private fun broadcast(server: MinecraftServer) {
        for (id in subscribers.toList()) {
            val player = server.playerList.getPlayer(id)
            val level = player?.level()
            if (player == null || level !is ServerLevel) {
                continue
            }
            if (!player.createCommandSourceStack().hasPermission(OPERATOR_LEVEL)) {
                subscribers.remove(id)
                continue
            }
            val entries =
                level
                    .getEntitiesOfClass(Villager::class.java, player.boundingBox.inflate(RADIUS))
                    .mapNotNull { (it as? NeedsDebugSource)?.villagerModDebugSnapshot() }
            ServerPlayNetworking.send(player, VillagerDebugPayload(entries))
        }
    }
}
