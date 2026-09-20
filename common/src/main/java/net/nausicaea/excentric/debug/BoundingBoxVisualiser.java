package net.nausicaea.excentric.debug;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

public final class BoundingBoxVisualiser {
	private BoundingBoxVisualiser() {
	}

	public static void showEdges(ServerLevel level, BoundingBox box, ParticleOptions particle, double spacing) {
		double minX = box.minX(), minY = box.minY(), minZ = box.minZ();
		double maxX = box.maxX() + 1, maxY = box.maxY() + 1, maxZ = box.maxZ() + 1;

		Vec3[] corners = {new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ),
		        new Vec3(minX, minY, maxZ), new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ),
		        new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ)};

		int[][] edges = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, // bottom
		        {4, 5}, {5, 6}, {6, 7}, {7, 4}, // top
		        {0, 4}, {1, 5}, {2, 6}, {3, 7} // verticals
		};

		for (int[] edge : edges) {
			drawEdge(level, particle, corners[edge[0]], corners[edge[1]], spacing);
		}
	}

	private static void drawEdge(ServerLevel level, ParticleOptions particle, Vec3 from, Vec3 to, double spacing) {
		double length = from.distanceTo(to);
		int steps = Math.max(1, (int) Math.ceil(length / spacing));
		for (int i = 0; i <= steps; i++) {
			Vec3 p = from.lerp(to, (double) i / steps);
			level.sendParticles(particle, p.x, p.y, p.z, 1, 0, 0, 0, 0);
		}
	}
}
