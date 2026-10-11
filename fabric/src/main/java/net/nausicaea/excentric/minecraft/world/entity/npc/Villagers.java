package net.nausicaea.excentric.minecraft.world.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.minecraft.world.level.VillageRefs;
import net.nausicaea.excentric.minecraft.world.level.Villages;

public final class Villagers {
    public static void onLoad(ServerLevel serverLevel, Villager villager) {
        if (VillageRefs.get(villager).isPresent()) {
            return;
        }

        Villages.get(serverLevel)
                .find(villager.blockPosition())
                .toOptional()
                .ifPresent(village -> VillageRefs.claim(villager, village.getId()));
    }

    private Villagers() {}
}
