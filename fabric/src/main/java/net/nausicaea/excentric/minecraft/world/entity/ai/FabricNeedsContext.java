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
import net.nausicaea.excentric.ExcentricServiceApi;
import net.nausicaea.excentric.minecraft.world.SimpleContainerUtils;
import net.nausicaea.excentric.minecraft.world.entity.EquipmentUserUtils;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.Service;
import net.nausicaea.excentric.minecraft.world.entity.ai.service.StackSource;

public record FabricNeedsContext(NeedsCollection needs, LivingEntity entity)
        implements NeedsContext {
    private static final int HALF_WIDTH = 5 << 16;
    private static final int HALF_HEIGHT = 2 << 16;
    private static final Vec3 SEARCH_MIN = new Vec3(-HALF_WIDTH, -HALF_HEIGHT, -HALF_WIDTH);
    private static final Vec3 SEARCH_MAX = new Vec3(HALF_WIDTH, HALF_HEIGHT, HALF_WIDTH);

    public Stream<Service> collect() {
        var level = entity.level();
        // The service collection is not designed to run on clients.
        if (!(level instanceof ServerLevel serverLevel)) {
            return Stream.empty();
        }

        ArrayList<Service> svcBuffer = new ArrayList<>();

        // If the entity has an inventory.
        if (entity instanceof InventoryCarrier ic) {
            var inventory = ic.getInventory();
            SimpleContainerUtils.getItems(inventory)
                    .flatMap(
                            stack ->
                                    ExcentricServiceApi.findItemService(
                                            stack.value(),
                                            entity,
                                            new StackSource.Inventory(inventory, stack.index()))
                                            .stream())
                    .forEach(svcBuffer::add);
        }

        // If the entity can carry equipment.
        if (entity instanceof EquipmentUser eu) {
            EquipmentUserUtils.getItemBySlot(eu, EquipmentSlot.MAINHAND)
                    .flatMap(
                            s ->
                                    ExcentricServiceApi.findItemService(
                                            s,
                                            entity,
                                            new StackSource.Held(EquipmentSlot.MAINHAND)))
                    .ifPresent(svcBuffer::add);
            EquipmentUserUtils.getItemBySlot(eu, EquipmentSlot.OFFHAND)
                    .flatMap(
                            s ->
                                    ExcentricServiceApi.findItemService(
                                            s, entity, new StackSource.Held(EquipmentSlot.OFFHAND)))
                    .ifPresent(svcBuffer::add);
        }

        // Find item entities lying around somewhere in the world
        var pos = entity.position();
        var aabb = new AABB(pos.add(SEARCH_MIN), pos.add(SEARCH_MAX));
        serverLevel.getEntitiesOfClass(ItemEntity.class, aabb).stream()
                .flatMap(
                        itemEntity ->
                                ExcentricServiceApi.findItemService(
                                        itemEntity.getItem(),
                                        entity,
                                        new StackSource.World(itemEntity))
                                        .stream())
                .forEach(svcBuffer::add);

        // TODO: Find services of items lying in containers somewhere.

        // TODO: Collect services from blocks in the world.

        // TODO: Collect services from other entities in the World.

        return svcBuffer.stream();
    }
}
