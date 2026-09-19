package net.nausicaea.excentric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.nausicaea.excentric.mixin.accessor.JigsawStructureAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Stream;

public final class VillageManager extends SavedData {
	private record Data(List<Village> villages) {
	}
	private static final Codec<Data> CODEC = RecordCodecBuilder.create(
	    i -> i.group(Codec.list(Village.CODEC).fieldOf("villages").forGetter(Data::villages)).apply(i, Data::new));
	private static final String DATA_NAME = ExcentricCommon.MOD_ID + "_villages";
	private static final Logger LOG = LoggerFactory.getLogger(VillageManager.class);
	private Map<UUID, Village> villages;

	public static VillageManager get(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(
		    // The third argument
		    // [net.minecraft.util.datafix.DataFixTypes] is null
		    // because we don't
		    // have any data migrations yet.
		    new SavedData.Factory<>(VillageManager::new, VillageManager::load, null), DATA_NAME);
	}

	public VillageManager() {
		this.villages = new HashMap<>();
	}

	/// Create a new village (radius and height from config)
	/// 1. Determine [net.nausicaea.excentric.Village#anchor()]
	/// 2. Determine all beds
	///    ([net.minecraft.world.entity.ai.village.poi.PoiTypes#HOME]) within
	///    [net.nausicaea.excentric.Village#radius()]
	/// 3. Calculate [net.nausicaea.excentric.Village#center()] from the beds
	public Village create(GlobalPos anchor) {
		// Village center = new Village(UUID.randomUUID(), this::setDirty, anchor);
		// villages.put(center.id(), center);
		// setDirty();
		// return center;
		throw new Todo();
	}

	public Optional<Village> find(GlobalPos anchor) {
		throw new Todo();
	}

	public Village findOrCreate(GlobalPos anchor) {
		return find(anchor).orElseGet(() -> create(anchor));
	}

	public Optional<Village> get(UUID id) {
		return Optional.ofNullable(villages.get(id));
	}

	private static List<StructureStart> findVillageStarts(StructureManager structureManager, ChunkPos chunkPos) {
		return structureManager.startsForStructure(chunkPos, structure -> {
			if (!(structure instanceof JigsawStructure jigsawStructure)) {
				return false;
			}
			// SAFETY: Final classes (JigsawStructure) must have the intermediate cast to
			// Object (see
			// https://docs.fabricmc.net/develop/mixins/accessors#accessors-for-final-classes).
			var startPool = ((JigsawStructureAccessor) (Object) jigsawStructure).villageMod$getStartPool();
			// The following call is true if the resource path starts with "village"
			return startPool.unwrapKey().map(key -> key.location().getPath().endsWith("town_centers")).orElse(false);
		});
	}

	/// 1. Find the structure start chunk with
	///    [net.minecraft.world.level.StructureManager#getStructureWithPieceAt]
	/// 2. Query [net.nausicaea.excentric.VillageManager#findOrCreate] for the
	///    closest [net.nausicaea.excentric.Village] in range or trigger creation of
	///    one.
	/// 3. Find [net.minecraft.world.entity.npc.Villager]s and
	///    [net.minecraft.world.level.block.entity.BellBlockEntity], and link the new
	///    [net.nausicaea.excentric.Village#id()].
	public void onChunkLoad(ServerLevel serverLevel, LevelChunk chunk) {
		var chunkPos = chunk.getPos();
		var dimension = serverLevel.dimension();
		var poiManager = serverLevel.getPoiManager();

		var structureStarts = findVillageStarts(serverLevel.structureManager(), chunkPos);

		if (structureStarts.size() > 1) {
			LOG.warn(ExcentricCommon.LOG_MARKER,
			    "Post-load identified {} matching village structures in chunk {}@{} but expected only one",
			    structureStarts.size(), chunkPos, dimension);
		}

		// Find all the bells in the chunk through PoiManager (assuming PoiManager
		// already has a populated index for the current chunk.
		// TODO: verify that PoiManager produces equivalent results to searching the
		// entire chunk.
		var bellsInChunk = poiManager
		    .getInChunk(poiType -> poiType == PoiTypes.MEETING, chunkPos, PoiManager.Occupancy.ANY)
		    .flatMap(poiRecord -> {
			    if (serverLevel.getBlockEntity(poiRecord.getPos()) instanceof BellBlockEntity bellBlockEntity) {
				    return Stream.of(bellBlockEntity);
			    }
			    return Stream.empty();
		    }).toList();

		// Find all villagers inside the chunk.
		var villagersInChunk = serverLevel.getEntitiesOfClass(Villager.class,
		    new AABB(chunkPos.getMinBlockX(), serverLevel.getMinY(), chunkPos.getMinBlockZ(),
		        chunkPos.getMaxBlockX() + 1, serverLevel.getMaxY(), chunkPos.getMaxBlockZ() + 1),
		    v -> new ChunkPos(v.blockPosition()).equals(chunkPos));

		// Assume there is only one village structure in this chunk.
		ListUtils.first(structureStarts)
		    // The first element starts the village.
		    .flatMap(s -> ListUtils.first(s.getPieces()))
		    .map(s -> findOrCreate(GlobalPos.of(dimension, s.getLocatorPosition()))).ifPresent(village -> {
			    // Link all bells to the village.
			    bellsInChunk.forEach(bbe -> ((VillageRef) bbe).villageMod$setVillageId(village.id()));
			    // Link all villagers to the village.
			    villagersInChunk.forEach(v -> ((VillageRef) v).villageMod$setVillageId(village.id()));
		    });
	}

	/// 1. Does the bell have a village [java.util.UUID]?
	/// 2. If yes, early return. This case is expected to be seldom.
	/// 3. If not, query [net.nausicaea.excentric.VillageManager#findOrCreate] for
	///    the closest [net.nausicaea.excentric.Village] in range or trigger creation
	///    of one.
	/// 4. Record the [net.nausicaea.excentric.Village#id()] on the bell.
	public void onBlockPlace(ServerLevel serverLevel, VillageRef villageRef, BlockPos blockPos) {
		// TODO: figure out if the village ID is persistent if the block is broken and
		// re-placed somewhere else.
		var villageIdPresent = villageRef.villageMod$getVillageId().map(id -> {
			LOG.warn(ExcentricCommon.LOG_MARKER, "Newly placed bell block at {} already has a reference to village {}",
			    blockPos, id);
			return true;
		}).orElse(false);
		if (villageIdPresent) {
			return;
		}

		var village = findOrCreate(GlobalPos.of(serverLevel.dimension(), blockPos));
		villageRef.villageMod$setVillageId(village.id());
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		var serialized = new Data(List.copyOf(villages.values()));
		return (CompoundTag) CODEC.encode(serialized, NbtOps.INSTANCE, tag).getPartialOrThrow();
	}

	private static VillageManager load(CompoundTag tag, HolderLookup.Provider registries) {
		VillageManager manager = new VillageManager();
		CODEC.decode(NbtOps.INSTANCE, tag)
		    .resultOrPartial(err -> LOG.error(ExcentricCommon.LOG_MARKER, "Failed to parse village data: {}", err))
		    .map(p -> p.getFirst().villages()).orElseGet(List::of).forEach(d -> manager.villages.put(d.id(), d));
		return manager;
	}
}
