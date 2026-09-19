package net.nausicaea.excentric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.mixin.accessor.JigsawStructureAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public final class VillageManager extends SavedData {
	private static final Codec<Data> CODEC = RecordCodecBuilder.create(
	    i -> i.group(Codec.list(Village.CODEC).fieldOf("villages").forGetter(Data::villages)).apply(i, Data::new));
	private static final String DATA_NAME = ExcentricCommon.MOD_ID + "_villages";
	private static final Logger LOG = LoggerFactory.getLogger(VillageManager.class);
	private static final int CHUNK_RADIUS = 4;
	private static final int SECTION_HEIGHT = 3;
	private final Map<UUID, Village> villages;
	public VillageManager() {
		this.villages = new HashMap<>();
	}

	public static VillageManager get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(
		    // The third argument
		    // [net.minecraft.util.datafix.DataFixTypes] is null
		    // because we don't
		    // have any data migrations yet.
		    new SavedData.Factory<>(VillageManager::new, VillageManager::load, null), DATA_NAME);
	}

	private static BoundingBox villageBoundingBox(BlockPos anchor, int chunkRadius, int sectionHeight) {
		var halfWidth = 8 * chunkRadius;
		var halfHeight = 8 * sectionHeight;
		return new BoundingBox(anchor.getX() - halfWidth, anchor.getY() - halfHeight, anchor.getZ() - halfWidth,
		    anchor.getX() + halfWidth, anchor.getY() + halfHeight, anchor.getZ() + halfWidth);
	}

	/// Find StructureStart instances for any villages that extend into the
	/// given chunk.
	private static List<StructureStart> findVillageStarts(StructureManager structureManager, ChunkPos chunkPos) {
		return structureManager.startsForStructure(chunkPos, structure -> {
			if (!(structure instanceof JigsawStructure jigsawStructure)) {
				return false;
			}
			// SAFETY: Final classes (JigsawStructure) must have the intermediate cast to
			// Object (see
			// https://docs.fabricmc.net/develop/mixins/accessors#accessors-for-final-classes).
			var startPool = ((JigsawStructureAccessor) (Object) jigsawStructure).villageMod$getStartPool();
			return startPool.unwrapKey().map(key -> key.location().getPath().endsWith("town_centers")).orElse(false);
		});
	}

	private static BoundingBox chunkBoundingBox(Level level, ChunkPos chunkPos) {
		return new BoundingBox(chunkPos.getMinBlockX(), level.getMinY(), chunkPos.getMinBlockZ(),
		    chunkPos.getMaxBlockX() + 1, level.getMaxY(), chunkPos.getMaxBlockZ() + 1);
	}

	/// Produce a function that reconciles important members of a chunk with a
	/// [Village]. FIXME: narrow reconciliation to chunk section instead of
	/// whole chunks.
	private static void reconcileChunk(ServerLevel serverLevel, ChunkPos chunkPos, Village village) {
		var poiManager = serverLevel.getPoiManager();

		// Find all the bells in the chunk through PoiManager (assuming PoiManager
		// already has a populated index for the current chunk.
		// TODO: verify that PoiManager produces equivalent results to searching the
		// entire chunk.
		var bellsInChunk = poiManager.getInChunk(PoiTypes.MEETING::equals, chunkPos, PoiManager.Occupancy.ANY)
		    .flatMap(poiRecord -> {
			    if (serverLevel.getBlockEntity(poiRecord.getPos()) instanceof BellBlockEntity bellBlockEntity) {
				    return Stream.of(bellBlockEntity);
			    }
			    return Stream.empty();
		    }).toList();

		// Find all villagers inside the chunk.
		var villagersInChunk = serverLevel.getEntitiesOfClass(Villager.class,
		    AABB.of(chunkBoundingBox(serverLevel, chunkPos)));

		if (bellsInChunk.isEmpty() && villagersInChunk.isEmpty()) {
			return;
		}

		LOG.debug(ExcentricCommon.LOG_MARKER, "Running chunk reconciliation for {} ({} bells and {} villagers)",
		    chunkPos, bellsInChunk.size(), villagersInChunk.size());
		var id = village.id();
		// Link all bells to the village.
		bellsInChunk.forEach(bbe -> ((VillageRef) bbe).villageMod$setVillageId(id));
		// Link all villagers to the village.
		villagersInChunk.forEach(v -> ((VillageRef) v).villageMod$setVillageId(id));
	}

	/// Calculate the global position of a structure piece.
	private static GlobalPos piecePos(ResourceKey<Level> dimension, StructurePiece piece) {
		return GlobalPos.of(dimension, piece.getLocatorPosition());
	}

	private static VillageManager load(CompoundTag tag, HolderLookup.Provider registries) {
		VillageManager manager = new VillageManager();
		CODEC.decode(NbtOps.INSTANCE, tag)
		    .resultOrPartial(err -> LOG.error(ExcentricCommon.LOG_MARKER, "Failed to parse village data: {}", err))
		    .map(p -> p.getFirst().villages()).orElseGet(List::of).forEach(d -> manager.villages.put(d.id(), d));
		return manager;
	}

	/// Claim a new village
	///
	/// 1. Determine [net.nausicaea.excentric.Village#anchor()]
	/// 2. Determine all beds
	///    ([net.minecraft.world.entity.ai.village.poi.PoiTypes#HOME]) within
	///    [CHUNK_RADIUS]
	/// 3. Calculate [net.nausicaea.excentric.Village#center()] from the beds
	/// 4. Search for any [Villager]s and
	///    [net.minecraft.world.level.block.BellBlock]s within the village extents
	///    and assigns them to this village.
	///
	/// TODO: what happens to villagers who spawn after claiming?
	public Village claim(ServerLevel level, GlobalPos anchor) {
		var poiManager = level.getPoiManager();
		var extents = villageBoundingBox(anchor.pos(), CHUNK_RADIUS, SECTION_HEIGHT);
		LOG.debug(ExcentricCommon.LOG_MARKER, "New village with volume centered at {} (dim: {}): {}x{}x{}",
		    anchor.pos(), anchor.dimension().location().getPath(), extents.getXSpan(), extents.getYSpan(),
		    extents.getZSpan());
		var homes = poiManager
		    .getInSquare(p -> p.is(PoiTypes.HOME), anchor.pos(), 16 * CHUNK_RADIUS, PoiManager.Occupancy.ANY)
		    .filter(p -> extents.isInside(p.getPos())).toList();
		LOG.debug(ExcentricCommon.LOG_MARKER, "Found {} homes / beds", homes.size());
		var centroid = Vec3Utils.toBlockPosFloor(
		    Vec3Utils.mapMean(homes, p -> new Vec3(p.getPos())).orElseGet(() -> new Vec3(anchor.pos())));
		var village = new Village(UUID.randomUUID(), anchor, centroid, extents);
		LOG.debug(ExcentricCommon.LOG_MARKER, "Claimed village {}", village);
		villages.put(village.id(), village);
		setDirty();

		var intersectingChunks = village.boundingBox().intersectingChunks().toList();
		LOG.debug(ExcentricCommon.LOG_MARKER, "Now starting reconciliation on {} chunks", intersectingChunks.size());
		// TODO: actually perform the reconciliation.
		// village.boundingBox()
		// .intersectingChunks()
		// .forEach(chunk -> reconcileChunk(level, chunk, village));

		return village;
	}

	// TODO: this silently discards multiple matching villages
	public Optional<Village> find(GlobalPos anchor) {
		return villages.values().stream()
		    .filter(v -> v.anchor().dimension().equals(anchor.dimension()) && v.boundingBox().isInside(anchor.pos()))
		    .findFirst();
	}

	public Village findOrClaim(ServerLevel level, GlobalPos anchor) {
		return find(anchor).orElseGet(() -> claim(level, anchor));
	}

	public Optional<Village> get(UUID id) {
		return Optional.ofNullable(villages.get(id));
	}

	/// 1. Find the structure start chunk with [findVillageStarts]
	/// 2. Query [net.nausicaea.excentric.VillageManager#findOrClaim] for the closest
	///    [net.nausicaea.excentric.Village] in range or trigger creation of one.
	/// 3. Find [net.minecraft.world.entity.npc.Villager]s and
	///    [net.minecraft.world.level.block.entity.BellBlockEntity], and link the new
	///    [net.nausicaea.excentric.Village#id()].
	public void onChunkLoad(ServerLevel serverLevel, LevelChunk chunk) {
		var loadedChunkPos = chunk.getPos();
		var dimension = serverLevel.dimension();

		var structureStarts = findVillageStarts(serverLevel.structureManager(), loadedChunkPos);

		if (structureStarts.size() > 1) {
			LOG.warn(ExcentricCommon.LOG_MARKER,
			    "Post-load identified {} matching village structures in chunk {}@{} but expected only one",
			    structureStarts.size(), loadedChunkPos, dimension);
		}

		// Assume there is only one village structure in the loaded chunk.
		ListUtils.first(structureStarts)
		    // The first element starts the village. Note that the start may not be in the
		    // currently loaded chunk. We're just using that information to record the
		    // [Village#anchor].
		    .flatMap(s -> ListUtils.first(s.getPieces())).map(s -> findOrClaim(serverLevel, piecePos(dimension, s)))
		    .ifPresent(village -> reconcileChunk(serverLevel, loadedChunkPos, village));
	}

	/// 1. Does the block have a village [java.util.UUID]?
	/// 2. If yes, early return. This case is expected to be seldom.
	/// 3. If not, query [net.nausicaea.excentric.VillageManager#findOrClaim] for the
	///    closest [net.nausicaea.excentric.Village] in range or trigger creation of
	///    one.
	/// 4. Record the [net.nausicaea.excentric.Village#id()] on the submitted block.
	public void onBlockPlace(ServerLevel serverLevel, VillageRef block, BlockPos blockPos) {
		// TODO: figure out if the village ID is persistent if the block is broken and
		// re-placed somewhere else.
		var villageIdPresent = block.villageMod$getVillageId().map(id -> {
			LOG.warn(ExcentricCommon.LOG_MARKER, "Newly placed bell block at {} already has a reference to village {}",
			    blockPos, id);
			return true;
		}).orElse(false);
		if (villageIdPresent) {
			return;
		}

		var village = findOrClaim(serverLevel, GlobalPos.of(serverLevel.dimension(), blockPos));
		block.villageMod$setVillageId(village.id());
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		var serialized = new Data(List.copyOf(villages.values()));
		return (CompoundTag) CODEC.encode(serialized, NbtOps.INSTANCE, tag).getPartialOrThrow();
	}

	private record Data(List<Village> villages) {
	}
}
