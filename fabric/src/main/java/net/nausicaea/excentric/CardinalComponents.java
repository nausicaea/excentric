package net.nausicaea.excentric;

import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.ladysnake.cca.api.v3.block.BlockComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.block.BlockComponentInitializer;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.chunk.ChunkComponentInitializer;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.level.LevelComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.level.LevelComponentInitializer;

public final class CardinalComponents
    implements
        LevelComponentInitializer,
        ChunkComponentInitializer,
        BlockComponentInitializer,
        EntityComponentInitializer {
	public static final ComponentKey<VillageManagerComponent> VILLAGE_MANAGER = ComponentRegistry
	    .getOrCreate(ExcentricCommon.id("village_manager"), VillageManagerComponent.class);
	public static final ComponentKey<LevelChunkComponent> LEVEL_CHUNK = ComponentRegistry
	    .getOrCreate(ExcentricCommon.id("level_chunk"), LevelChunkComponent.class);
	public static final ComponentKey<BellComponent> BELL = ComponentRegistry.getOrCreate(ExcentricCommon.id("bell"),
	    BellComponent.class);
	public static final ComponentKey<VillagerComponent> VILLAGER = ComponentRegistry
	    .getOrCreate(ExcentricCommon.id("villager"), VillagerComponent.class);

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(VILLAGE_MANAGER, VillageManagerComponent::new);
	}

	@Override
	public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
		registry.register(LEVEL_CHUNK, LevelChunkComponent::new);
	}

	@Override
	public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
		registry.registerFor(BellBlockEntity.class, BELL, BellComponent::new);
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.registerFor(Villager.class, VILLAGER, VillagerComponent::new);
	}
}
