package net.nausicaea.excentric.minecraft.world.entity.ai;

import java.util.ArrayList;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentUser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.nausicaea.excentric.ai.NeedsCollection;
import net.nausicaea.excentric.ai.NeedsContext;
import net.nausicaea.excentric.ai.service.Held;
import net.nausicaea.excentric.ai.service.Inventory;
import net.nausicaea.excentric.ai.service.Service;
import net.nausicaea.excentric.ai.service.World;
import net.nausicaea.excentric.fabric.api.lookup.ServiceApi;
import net.nausicaea.excentric.minecraft.world.SimpleContainerExtKt;
import net.nausicaea.excentric.minecraft.world.entity.EquipmentUserExtKt;
import net.nausicaea.excentric.util.OptionKt;
import org.jetbrains.annotations.NotNull;

public record FabricNeedsContext(NeedsCollection needs, LivingEntity entity)
        implements NeedsContext {
    private static final int HALF_WIDTH = 5 << 16;
    private static final int HALF_HEIGHT = 2 << 16;
    private static final Vec3 SEARCH_MIN = new Vec3(-HALF_WIDTH, -HALF_HEIGHT, -HALF_WIDTH);
    private static final Vec3 SEARCH_MAX = new Vec3(HALF_WIDTH, HALF_HEIGHT, HALF_WIDTH);

    public @NotNull Stream<Service> collect() {
        var level = entity.level();
        // The service collection is not designed to run on clients.
        if (!(level instanceof ServerLevel serverLevel)) {
            return Stream.empty();
        }

        ArrayList<Service> svcBuffer = new ArrayList<>();

        // If the entity has an inventory.
        if (entity instanceof InventoryCarrier ic) {
            var inventory = ic.getInventory();
            SimpleContainerExtKt.stream(inventory)
                    .flatMap(
                            stack ->
                                    ServiceApi.findItemService(
                                            stack.getValue(),
                                            entity,
                                            new Inventory(inventory, stack.getIndex()))
                                            .toOptional()
                                            .stream())
                    .forEach(svcBuffer::add);
        }

        // If the entity can carry equipment.
        if (entity instanceof EquipmentUser eu) {
            OptionKt.andThen(
                            EquipmentUserExtKt.getItemBySlot(eu, EquipmentSlot.MAINHAND),
                            s ->
                                    ServiceApi.findItemService(
                                            s, entity, new Held(EquipmentSlot.MAINHAND)))
                    .toOptional()
                    .ifPresent(svcBuffer::add);
            OptionKt.andThen(
                            EquipmentUserExtKt.getItemBySlot(eu, EquipmentSlot.OFFHAND),
                            s ->
                                    ServiceApi.findItemService(
                                            s, entity, new Held(EquipmentSlot.OFFHAND)))
                    .toOptional()
                    .ifPresent(svcBuffer::add);
        }

        // Find item entities lying around somewhere in the world
        var pos = entity.position();
        var aabb = new AABB(pos.add(SEARCH_MIN), pos.add(SEARCH_MAX));
        serverLevel.getEntitiesOfClass(ItemEntity.class, aabb).stream()
                .flatMap(
                        itemEntity ->
                                ServiceApi.findItemService(
                                        itemEntity.getItem(), entity, new World(itemEntity))
                                        .toOptional()
                                        .stream())
                .forEach(svcBuffer::add);

        // TODO: Find services of items lying in containers somewhere.

        // TODO: Collect services from blocks in the world.

        // TODO: Collect services from other entities in the World.

        return svcBuffer.stream();
    }
}
