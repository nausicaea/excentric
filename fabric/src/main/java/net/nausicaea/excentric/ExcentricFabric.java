package net.nausicaea.excentric;

import net.fabricmc.api.ModInitializer;
import net.nausicaea.excentric.fabric.api.command.ExcentricCommands;
import net.nausicaea.excentric.fabric.api.event.EventCallbacks;
import net.nausicaea.excentric.fabric.api.lookup.ServiceApi;
import net.nausicaea.excentric.fabric.api.network.VillagerDebugBroadcaster;

public class ExcentricFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ServiceApi.register();
        EventCallbacks.register();
        VillagerDebugBroadcaster.register();
        ExcentricCommands.register();
    }
}
