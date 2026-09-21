package net.nausicaea.excentric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public final class Vec3Utils {
	private Vec3Utils() {
	}
	public static BlockPos toBlockPosFloor(Vec3 src) {
		return new BlockPos((int) src.x(), (int) src.y(), (int) src.z());
	}

	public static Vec3 div(Vec3 src, float f) {
		return new Vec3(src.x() / f, src.y() / f, src.z() / f);
	}

	public static Vec3 sum(Stream<Vec3> src) {
		return src.reduce(Vec3.ZERO, Vec3::add);
	}

	public static <T> Optional<Vec3> mapMean(List<T> src, Function<T, Vec3> mapFn) {
		return src.isEmpty()
		    ? Optional.empty()
		    : Optional.of(Vec3Utils.div(Vec3Utils.sum(src.stream().map(mapFn)), src.size()));
	}
}
