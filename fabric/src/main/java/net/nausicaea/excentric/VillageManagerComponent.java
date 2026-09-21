package net.nausicaea.excentric;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.debug.BoundingBoxVisualiser;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class VillageManagerComponent implements ServerTickingComponent, VillageManager {
	private static final Logger LOG = LoggerFactory.getLogger(VillageManagerComponent.class);
	private static final int CHUNK_RADIUS = 4;
	private static final int SECTION_HEIGHT = 3;

	private final LevelData levelData;
	private final Map<UUID, Village> villages;
	private final AtomicInteger tickCounter;

	public VillageManagerComponent(LevelData levelData) {
		this.levelData = levelData;
		this.villages = new HashMap<>();
		this.tickCounter = new AtomicInteger(0);
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		var villages = CODEC.decode(NbtOps.INSTANCE, tag)
		    .resultOrPartial(err -> LOG.error(ExcentricCommon.MARKER, "Failed to parse village data: {}", err))
		    .map(p -> p.getFirst().villages()).orElseGet(List::of);
		this.villages.clear();
		for (Village village : villages) {
			this.villages.put(village.id(), village);
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		var serialized = new Data(List.copyOf(this.villages.values()));
		var newTag = (CompoundTag) CODEC.encodeStart(NbtOps.INSTANCE, serialized).getPartialOrThrow();
		tag.merge(newTag);
	}

	@Override
	public void serverTick() {
		var ctr = tickCounter.getAndIncrement();
		if (ctr % 10 == 0) {
			ServerAccess.get().ifPresent(this::debug);
			tickCounter.set(0);
		}
	}

	@Override
	public Village claim(ServerLevel level, GlobalPos anchor) {
		var extents = Village.extents(anchor.pos(), CHUNK_RADIUS, SECTION_HEIGHT);
		return claim(level, anchor, extents);
	}

	/// Claim a new village
	///
	/// 1. Determine [net.nausicaea.excentric.Village#anchor()]
	/// 2. Determine all beds
	///    ([net.minecraft.world.entity.ai.village.poi.PoiTypes#HOME]) within
	///    [CHUNK_RADIUS]
	/// 3. Calculate [net.nausicaea.excentric.Village#center()] from the beds
	/// 4. Claim only the chunks within the bounding box of the village.
	///
	/// TODO: what happens to villagers who spawn after claiming?
	@Override
	public Village claim(ServerLevel level, GlobalPos anchor, BoundingBox extents) {
		var poiManager = level.getPoiManager();
		LOG.info(ExcentricCommon.MARKER, "New village with volume centered at {} (dim: {}): {}x{}x{}", anchor.pos(),
		    anchor.dimension().location().getPath(), extents.getXSpan(), extents.getYSpan(), extents.getZSpan());
		var homes = poiManager
		    .getInSquare(p -> p.is(PoiTypes.HOME), anchor.pos(), 16 * CHUNK_RADIUS, PoiManager.Occupancy.ANY)
		    .filter(p -> extents.isInside(p.getPos())).toList();
		LOG.info(ExcentricCommon.MARKER, "Found {} homes / beds", homes.size());
		var centroid = Vec3Utils.toBlockPosFloor(
		    Vec3Utils.mapMean(homes, p -> new Vec3(p.getPos())).orElseGet(() -> new Vec3(anchor.pos())));
		var village = new Village(UUID.randomUUID(), anchor, centroid, extents);
		villages.put(village.id(), village);
		claimLoadedChunks(level, village);
		return village;
	}

	/// This silently discards multiple matching villages
	@Override
	public Optional<Village> find(GlobalPos anchor) {
		return villages.values().stream()
		    .filter(v -> v.anchor().dimension().equals(anchor.dimension()) && v.boundingBox().isInside(anchor.pos()))
		    .findFirst();
	}

	/// Set [VillageRefComponent#villageId()] for all loaded chunks within the
	/// [Village] bounding box. Silently skips chunks that aren't fully loaded.
	private static void claimLoadedChunks(MinecraftServer server, Village village) {
		var level = server.getLevel(village.anchor().dimension());
		if (level == null) {
			return;
		}
		claimLoadedChunks(level, village);
	}

	/// Set [VillageRefComponent#villageId()] for all loaded chunks within the
	/// [Village] bounding box. Silently skips chunks that aren't fully loaded.
	private static void claimLoadedChunks(ServerLevel level, Village village) {
		var chunkSource = level.getChunkSource();
		village.boundingBox().intersectingChunks().filter(chunk -> chunkSource.hasChunk(chunk.x, chunk.z))
		    .flatMap(chunk -> Optional.ofNullable(chunkSource.getChunk(chunk.x, chunk.z, ChunkStatus.FULL, false))
		        .flatMap(CardinalComponents.LEVEL_CHUNK::maybeGet).stream())
		    .forEach(chunk -> chunk.setVillageId(village.id()));
	}

	private void debug(MinecraftServer server) {
		int particleColor = 0x0088ff;
		villages.values().stream()
		    .flatMap(
		        v -> Optional.ofNullable(server.getLevel(v.anchor().dimension())).map(l -> new Pair<>(l, v)).stream())
		    .forEach(v -> {
			    BoundingBoxVisualiser.showEdges(v.getFirst(), v.getSecond().boundingBox(),
			        new DustParticleOptions(particleColor, 1), 1);
		    });
	}

	private static final Codec<Data> CODEC = RecordCodecBuilder.create(
	    i -> i.group(Codec.list(Village.CODEC).fieldOf("villages").forGetter(Data::villages)).apply(i, Data::new));

	private record Data(List<Village> villages) {
	}
}
