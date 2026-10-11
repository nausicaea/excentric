package net.nausicaea.excentric.minecraft.world.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.nausicaea.excentric.fabric.api.attachment.VillageRefs;
import net.nausicaea.excentric.minecraft.world.level.Villages;

public final class Bells {
    public static void onPlace(ServerLevel level, BlockPos pos, BellBlockEntity bbe) {
        if (VillageRefs.get(bbe).isSome()) {
            return;
        }
        var village = Villages.Companion.get(level).findOrClaim(level, pos);
        VillageRefs.claim(bbe, village.getId());
    }

    public static void onLoad(ServerLevel level, BellBlockEntity bbe) {
        if (VillageRefs.get(bbe).isSome()) {
            return;
        }
        Villages.Companion.get(level)
                .find(bbe.getBlockPos())
                .toOptional()
                .ifPresent(v -> VillageRefs.claimDeferred(bbe, v.getId()));
    }

    private Bells() {}
}
