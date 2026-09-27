package net.nausicaea.excentric.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.*;
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BoundingBoxVisualiser;
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BoundingBoxUtils;
import net.nausicaea.excentric.minecraft.world.phys.Vec3Utils;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class VillageManagerComponent implements Component, VillageManager {
	private static final Logger LOG = LoggerFactory.getLogger(VillageManagerComponent.class);
	private static final int CHUNK_RADIUS = 4;
	private static final int SECTION_HEIGHT = 3;

	private final Level level;
	private final Map<UUID, Village> villages;

	public VillageManagerComponent(Level level) {
		this.level = level;
		this.villages = new HashMap<>();
	}

	@Override
	public void readFromNbt(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registryLookup) {
		var villages = CODEC.decode(NbtOps.INSTANCE, tag)
		    .resultOrPartial(err -> LOG.error(ExcentricCommon.MARKER, "Failed to parse village data: {}", err))
		    .map(p -> p.getFirst().villages()).orElseGet(List::of);
		this.villages.clear();
		for (Village village : villages) {
			this.villages.put(village.id(), village);
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag, @NotNull HolderLookup.Provider registryLookup) {
		var serialized = new Data(List.copyOf(this.villages.values()));
		var newTag = (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, serialized).getPartialOrThrow();
		tag.merge(newTag);
	}

	@Override
	public Village claim(ServerLevel level, BlockPos anchor) {
		var extents = VillageManagerUtils.extents(anchor, CHUNK_RADIUS, SECTION_HEIGHT);
		return claim(level, anchor, extents);
	}

	/// Claim a new village
	///
	/// 1. Determine [Village#anchor()]
	/// 2. Determine all beds
	///    ([net.minecraft.world.entity.ai.village.poi.PoiTypes#HOME]) within
	///    [CHUNK_RADIUS]
	/// 3. Calculate [Village#center()] from the beds
	/// 4. Claim only the chunks within the bounding box of the village.
	@Override
	public Village claim(ServerLevel level, BlockPos anchor, BoundingBox extents) {
		var poiManager = level.getPoiManager();
		LOG.info(ExcentricCommon.MARKER, "New village with volume centered at {}: {}x{}x{}", anchor, extents.getXSpan(),
		    extents.getYSpan(), extents.getZSpan());
		var homes = poiManager
		    .getInSquare(p -> p.is(PoiTypes.HOME), anchor, 16 * CHUNK_RADIUS, PoiManager.Occupancy.ANY)
		    .filter(p -> extents.isInside(p.getPos())).toList();
		LOG.info(ExcentricCommon.MARKER, "Found {} homes / beds", homes.size());
		var centroid = Vec3Utils
		    .toBlockPosFloor(Vec3Utils.mapMean(homes, p -> new Vec3(p.getPos())).orElseGet(() -> new Vec3(anchor)));
		var village = new Village(UUID.randomUUID(), anchor, centroid, extents);
		villages.put(village.id(), village);
		claimLoadedChunks(level, village);
		return village;
	}

	/// This silently discards multiple matching villages
	@Override
	public Optional<Village> find(BlockPos anchor) {
		return villages.values().stream().filter(v -> v.boundingBox().isInside(anchor)).findFirst();
	}

	/// Set [VillageRefComponent#villageId()] for all loaded chunks within the
	/// [Village] bounding box. Silently skips chunks that aren't fully loaded.
	private static void claimLoadedChunks(ServerLevel level, Village village) {
		var chunkSource = level.getChunkSource();
		BoundingBoxUtils.containedChunks(village.boundingBox()).filter(chunk -> chunkSource.hasChunk(chunk.x, chunk.z))
		    .flatMap(chunk -> Optional.ofNullable(chunkSource.getChunkNow(chunk.x, chunk.z))
		        .flatMap(CardinalComponents.LEVEL_CHUNK::maybeGet).stream())
		    .forEach(chunk -> chunk.setVillageId(village.id()));
	}

	public void debug(ServerLevel level) {
		int particleColor = 0x0088ff;
		villages.values().forEach(
		    v -> BoundingBoxVisualiser.showEdges(level, v.boundingBox(), new DustParticleOptions(particleColor, 1), 1));
	}

	private static final Codec<Data> CODEC = RecordCodecBuilder.create(
	    i -> i.group(Codec.list(Village.CODEC).fieldOf("villages").forGetter(Data::villages)).apply(i, Data::new));

	private record Data(List<Village> villages) {
	}
}
