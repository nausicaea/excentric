package net.nausicaea.excentric.fabric.api.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.nausicaea.excentric.ai.debug.VillagerDebug
import net.nausicaea.excentric.id

/**
 * Server-to-client payload that carries the debug snapshots of all villagers around a player. Each payload replaces
 * the previous one; an empty payload clears the display.
 */
data class VillagerDebugPayload(
    val entries: List<VillagerDebug>,
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<VillagerDebugPayload> = CustomPacketPayload.Type(id("villager_debug"))

        val CODEC: StreamCodec<ByteBuf, VillagerDebugPayload> =
            StreamCodec.of<ByteBuf, VillagerDebugPayload>(
                { buf, value ->
                    FriendlyByteBuf(buf).writeVarInt(value.entries.size)
                    value.entries.forEach { VillagerDebug.STREAM_CODEC.encode(buf, it) }
                },
                { buf ->
                    val size = FriendlyByteBuf(buf).readVarInt()
                    VillagerDebugPayload(List(size) { VillagerDebug.STREAM_CODEC.decode(buf) })
                },
            )
    }
}
