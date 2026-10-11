package net.nausicaea.excentric.minecraft.world.entity.npc;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.nausicaea.excentric.fabric.api.attachment.VillageRef;
import net.nausicaea.excentric.minecraft.world.level.Villages;

public final class Villagers {
    public static void onLoad(ServerLevel serverLevel, Villager villager) {
        if (VillageRef.get(villager).isSome()) {
            return;
        }

        Villages.Companion.get(serverLevel)
                .find(villager.blockPosition())
                .toOptional()
                .ifPresent(village -> VillageRef.claim(villager, village.getId()));
    }

    private Villagers() {}
}
