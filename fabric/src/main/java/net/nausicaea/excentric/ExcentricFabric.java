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
import net.minecraft.world.level.chunk.ChunkAccess;
import net.nausicaea.excentric.fabric.api.attachment.VillageRef;
import net.nausicaea.excentric.fabric.api.event.EventsKt;
import net.nausicaea.excentric.fabric.api.lookup.ServiceApi;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.util.EveryN;
import net.nausicaea.excentric.util.OptionKt;

public class ExcentricFabric implements ModInitializer {
    private static final EveryN every10Ticks = new EveryN(8);

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(ExcentricFabric::onEndTick);
        ServerChunkEvents.CHUNK_LOAD.register(ExcentricFabric::onChunkLoad);
        ServerBlockEntityEvents.BLOCK_ENTITY_LOAD.register(ExcentricFabric::onBlockEntityLoad);
        ServerEntityEvents.ENTITY_LOAD.register(ExcentricFabric::onEntityLoad);
        EventsKt.AFTER_BLOCK_PLACE.register(ExcentricFabric::onAfterBlockPlace);
        ServiceApi.register();
    }

    private static void onEndTick(MinecraftServer server) {
        VillageRef.drainDeferred();
        every10Ticks.run(
                () ->
                        server.getAllLevels()
                                .forEach(level -> Villages.Companion.get(level).debug(level)));
    }

    private static void onChunkLoad(ServerLevel level, ChunkAccess chunk) {
        if (VillageRef.get(chunk).isSome()) {
            return;
        }

        var vman = Villages.Companion.get(level);
        var chunkPos = chunk.getPos();
        OptionKt.orElse(
                        vman.find(chunkPos.getMiddleBlockPosition(80)),
                        () -> vman.findOrClaimByStructure(level, chunkPos))
                .toOptional()
                // Claim the chunk. Don't reconcile the chunk (i.e. claim anything that resides
                // on the chunk itself) here because it will trigger a complete chunk load.
                .ifPresent(village -> VillageRef.claim(chunk, village.getId()));
    }

    private static void onAfterBlockPlace(
            BlockState prevState, BlockPos pos, ServerLevel serverLevel) {
        if (!(serverLevel.getBlockEntity(pos) instanceof BellBlockEntity bbe)) {
            return;
        }
        if (VillageRef.get(bbe).isSome()) {
            return;
        }
        var village = Villages.Companion.get(serverLevel).findOrClaim(serverLevel, pos);
        VillageRef.claim(bbe, village.getId());
    }

    private static void onBlockEntityLoad(BlockEntity blockEntity, ServerLevel serverLevel) {
        if (!(blockEntity instanceof BellBlockEntity bbe)) {
            return;
        }
        if (VillageRef.get(bbe).isSome()) {
            return;
        }
        Villages.Companion.get(serverLevel)
                .find(bbe.getBlockPos())
                .toOptional()
                .ifPresent(v -> VillageRef.claimDeferred(bbe, v.getId()));
    }

    private static void onEntityLoad(Entity entity, ServerLevel serverLevel) {
        if (!(entity instanceof Villager villager)) {
            return;
        }
        if (VillageRef.get(villager).isSome()) {
            return;
        }

        Villages.Companion.get(serverLevel)
                .find(villager.blockPosition())
                .toOptional()
                .ifPresent(village -> VillageRef.claim(villager, village.getId()));
    }
}
