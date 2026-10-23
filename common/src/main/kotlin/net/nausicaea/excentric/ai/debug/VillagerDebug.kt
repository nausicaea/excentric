package net.nausicaea.excentric.ai.debug

import io.netty.buffer.ByteBuf
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation

/**
 * The satisfaction of a single [net.nausicaea.excentric.ai.need.Need], as shown by debug displays.
 */
data class NeedDebug(
    val key: ResourceLocation,
    val satisfaction: Double,
)

/**
 * Describes the currently active [net.nausicaea.excentric.ai.service.Service] of an entity, as shown by debug displays.
 */
data class ServiceDebug(
    /**
     * The need that the service addresses.
     */
    val need: ResourceLocation,
    /**
     * The simple class name of the service implementation.
     */
    val type: String,
    /**
     * See [net.nausicaea.excentric.ai.service.Service.describe].
     */
    val description: String,
    /**
     * See [net.nausicaea.excentric.ai.service.Service.baseUtility].
     */
    val baseUtility: Double,
    /**
     * The subjective score of the service: the result of the need's utility function, evaluated right now.
     */
    val score: Double,
    /**
     * The selection weight that the service had when it was selected (the score after the Boltzmann transform).
     */
    val weight: Double,
)

/**
 * A snapshot of an entity's needs and its active service. Intended to be synchronised to clients for debugging.
 */
data class VillagerDebug(
    val entityId: Int,
    val needs: List<NeedDebug>,
    val service: ServiceDebug?,
) {
    companion object {
        /**
         * Serialisation codec for network synchronisation.
         */
        val STREAM_CODEC: StreamCodec<ByteBuf, VillagerDebug> =
            StreamCodec.of<ByteBuf, VillagerDebug>(
                { buf, value -> encode(buf, value) },
                { buf -> decode(buf) },
            )

        private fun encode(
            buf: ByteBuf,
            value: VillagerDebug,
        ) {
            val b = FriendlyByteBuf(buf)
            b.writeVarInt(value.entityId)
            b.writeVarInt(value.needs.size)
            for (need in value.needs) {
                b.writeResourceLocation(need.key)
                b.writeDouble(need.satisfaction)
            }
            val svc = value.service
            b.writeBoolean(svc != null)
            if (svc != null) {
                b.writeResourceLocation(svc.need)
                b.writeUtf(svc.type)
                b.writeUtf(svc.description)
                b.writeDouble(svc.baseUtility)
                b.writeDouble(svc.score)
                b.writeDouble(svc.weight)
            }
        }

        private fun decode(buf: ByteBuf): VillagerDebug {
            val b = FriendlyByteBuf(buf)
            val entityId = b.readVarInt()
            val needs = List(b.readVarInt()) { NeedDebug(b.readResourceLocation(), b.readDouble()) }
            val service =
                if (b.readBoolean()) {
                    ServiceDebug(
                        need = b.readResourceLocation(),
                        type = b.readUtf(),
                        description = b.readUtf(),
                        baseUtility = b.readDouble(),
                        score = b.readDouble(),
                        weight = b.readDouble(),
                    )
                } else {
                    null
                }
            return VillagerDebug(entityId, needs, service)
        }
    }
}
