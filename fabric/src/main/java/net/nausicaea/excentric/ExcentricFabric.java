package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nausicaea.excentric.fabric.api.attachment.VillageRefs;
import net.nausicaea.excentric.fabric.api.event.EventsKt;
import net.nausicaea.excentric.minecraft.world.block.entity.Bells;
import net.nausicaea.excentric.minecraft.world.entity.npc.Villagers;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.minecraft.world.level.chunk.LevelChunks;
import net.nausicaea.excentric.util.EveryN;

public class ExcentricFabric implements ModInitializer {
    private static final EveryN every10Ticks = new EveryN(8);

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(ExcentricFabric::onEndTick);
        ServerChunkEvents.CHUNK_LOAD.register(LevelChunks::onLoad);
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(ExcentricFabric::onBlockEntityLoad);
        ServerEntityEvents.ENTITY_LOAD.register(ExcentricFabric::onEntityLoad);
        EventsKt.AFTER_BLOCK_PLACE.register(ExcentricFabric::onAfterBlockPlace);
        ExcentricServiceApi.register();
    }

    private static void onEndTick(MinecraftServer server) {
        VillageRefs.drainDeferred();
        every10Ticks.run(
                () ->
                        server.getAllLevels()
                                .forEach(level -> Villages.Companion.get(level).debug(level)));
    }

    private static void onAfterBlockPlace(
            BlockState prevState, BlockPos pos, ServerLevel serverLevel) {
        if (!(serverLevel.getBlockEntity(pos) instanceof BellBlockEntity bbe)) {
            return;
        }
        Bells.onPlace(serverLevel, pos, bbe);
    }

    private static void onBlockEntityLoad(BlockEntity blockEntity, ServerLevel serverLevel) {
        if (!(blockEntity instanceof BellBlockEntity bbe)) {
            return;
        }
        Bells.onLoad(serverLevel, bbe);
    }

    private static void onEntityLoad(Entity entity, ServerLevel serverLevel) {
        if (!(entity instanceof Villager villager)) {
            return;
        }
        Villagers.onLoad(serverLevel, villager);
    }
}
