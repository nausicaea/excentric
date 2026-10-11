package net.nausicaea.excentric.minecraft.world.phys

import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3
import net.nausicaea.excentric.util.None
import net.nausicaea.excentric.util.Option
import net.nausicaea.excentric.util.Some
import java.util.function.Function
import java.util.stream.Stream

fun Vec3.toBlockPosFloor(): BlockPos = BlockPos(this.x().toInt(), this.y().toInt(), this.z().toInt())

fun Vec3.div(f: Float): Vec3 = Vec3(this.x() / f, this.y() / f, this.z() / f)

fun Stream<Vec3>.sum(): Vec3 = this.reduce(Vec3.ZERO, Vec3::add)

fun <T> List<T>.mapMean(mapFn: (T) -> Vec3): Option<Vec3> =
    if (this.isEmpty()) {
        None
    } else {
        Some(
            this
                .stream()
                .map(mapFn)
                .sum()
                .div(this.size.toFloat()),
        )
    }
