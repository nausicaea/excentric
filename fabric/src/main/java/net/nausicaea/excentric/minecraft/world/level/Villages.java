package net.nausicaea.excentric.minecraft.world.level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.*;
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BoundingBoxVisualiser;
import net.nausicaea.excentric.minecraft.world.level.levelgen.structure.BoundingBoxUtils;
import net.nausicaea.excentric.minecraft.world.phys.Vec3Utils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class Villages extends SavedData implements VillageManager {
	private static final Logger LOG = LoggerFactory.getLogger(Villages.class);
	private static final String ID = ExcentricCommon.MOD_ID + "_villages";
	@SuppressWarnings("DataFlowIssue")
	@SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION", justification = "We don't use any data fixer uppers (i.e. migration types), so this is supposed to be null.")
	private static final SavedData.Factory<Villages> FACTORY = new SavedData.Factory<>(Villages::new, Villages::load,
	    null);
	private static final int CHUNK_RADIUS = 4;
	private static final int SECTION_HEIGHT = 3;

	private final Map<UUID, Village> villages = new HashMap<>();

	public static Villages get(ServerLevel level) {
		return level.getDataStorage().computeIfAbsent(FACTORY, ID);
	}

	@NotNull
	@Override
	public CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider) {
		var serialized = new Data(List.copyOf(this.villages.values()));
		return (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, serialized).getPartialOrThrow();
	}

	private static Villages load(CompoundTag tag, HolderLookup.Provider registries) {
		var state = new Villages();
		CODEC.parse(NbtOps.INSTANCE, tag)
		    .resultOrPartial(err -> LOG.error(ExcentricCommon.MARKER, "Failed to parse village data: {}", err))
		    .ifPresent(data -> data.villages().forEach(v -> state.villages.put(v.id(), v)));
		return state;
	}

	@Override
	public Village claim(ServerLevel level, BlockPos anchor) {
		var extents = VillageUtils.extents(anchor, CHUNK_RADIUS, SECTION_HEIGHT);
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

	private static void claimLoadedChunks(ServerLevel level, Village village) {
		var chunkSource = level.getChunkSource();
		var villageId = village.id();
		BoundingBoxUtils.containedChunks(village.boundingBox())
		    .filter(chunkPos -> ServerChunkCacheUtils.hasChunk(chunkSource, chunkPos))
		    .flatMap(chunkPos -> ServerChunkCacheUtils.getChunkNow(chunkSource, chunkPos).stream())
		    .forEach(chunk -> VillageRefs.claim(chunk, villageId));
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
