package net.nausicaea.excentric.minecraft.world.level.chunk;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.nausicaea.excentric.fabric.api.attachment.VillageRefs;
import net.nausicaea.excentric.minecraft.world.level.Villages;
import net.nausicaea.excentric.util.OptionKt;

public final class LevelChunks {
    public static void onLoad(ServerLevel level, ChunkAccess chunk) {
        if (VillageRefs.get(chunk).isSome()) {
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
                .ifPresent(village -> VillageRefs.claim(chunk, village.getId()));
    }

    private LevelChunks() {}
}
