package net.nausicaea.excentric.minecraft.world.level.levelgen.structure

import io.netty.buffer.ByteBuf
import net.minecraft.core.SectionPos
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.Vec3
import java.util.stream.Stream
import kotlin.math.ceil
import kotlin.math.max

fun BoundingBox.containedChunks(): Stream<ChunkPos> {
    val i = SectionPos.blockToSectionCoord(this.minX() + 15)
    val j = SectionPos.blockToSectionCoord(this.minZ() + 15)
    val k = SectionPos.blockToSectionCoord(this.maxX() + 1) - 1
    val l = SectionPos.blockToSectionCoord(this.maxZ() + 1) - 1
    return ChunkPos.rangeClosed(ChunkPos(i, j), ChunkPos(k, l))
}

fun BoundingBox.showEdges(
    level: ServerLevel,
    particle: ParticleOptions,
    spacing: Double,
) {
    val minX = this.minX().toDouble()
    val minY = this.minY().toDouble()
    val minZ = this.minZ().toDouble()
    val maxX = (this.maxX() + 1).toDouble()
    val maxY = (this.maxY() + 1).toDouble()
    val maxZ = (this.maxZ() + 1).toDouble()

    val corners =
        arrayOf<Vec3>(
            Vec3(minX, minY, minZ),
            Vec3(maxX, minY, minZ),
            Vec3(maxX, minY, maxZ),
            Vec3(minX, minY, maxZ),
            Vec3(minX, maxY, minZ),
            Vec3(maxX, maxY, minZ),
            Vec3(maxX, maxY, maxZ),
            Vec3(minX, maxY, maxZ),
        )

    val edges =
        arrayOf(
            intArrayOf(0, 1),
            intArrayOf(1, 2),
            intArrayOf(2, 3),
            intArrayOf(3, 0), // bottom
            intArrayOf(4, 5),
            intArrayOf(5, 6),
            intArrayOf(6, 7),
            intArrayOf(7, 4), // top
            intArrayOf(0, 4),
            intArrayOf(1, 5),
            intArrayOf(2, 6),
            intArrayOf(3, 7), // verticals
        )

    for (edge in edges) {
        drawEdge(level, particle, corners[edge[0]], corners[edge[1]], spacing)
    }
}

private fun drawEdge(
    level: ServerLevel,
    particle: ParticleOptions,
    from: Vec3,
    to: Vec3,
    spacing: Double,
) {
    val length = from.distanceTo(to)
    val steps = max(1, ceil(length / spacing).toInt())
    for (i in 0..steps) {
        val p = from.lerp(to, i.toDouble() / steps)
        level.sendParticles(particle, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0)
    }
}

val BOUNDING_BOX_STREAM_CODEC: StreamCodec<ByteBuf, BoundingBox> =
    StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        BoundingBox::minX,
        ByteBufCodecs.VAR_INT,
        BoundingBox::minY,
        ByteBufCodecs.VAR_INT,
        BoundingBox::minZ,
        ByteBufCodecs.VAR_INT,
        BoundingBox::maxX,
        ByteBufCodecs.VAR_INT,
        BoundingBox::maxY,
        ByteBufCodecs.VAR_INT,
        BoundingBox::maxZ,
        ::BoundingBox,
    )
