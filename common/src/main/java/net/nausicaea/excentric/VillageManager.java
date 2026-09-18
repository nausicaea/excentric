package net.nausicaea.excentric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
		return find(anchor).orElseGet(() -> this.create(anchor));
	}

	public Optional<Village> get(UUID id) {
		return Optional.ofNullable(villages.get(id));
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
