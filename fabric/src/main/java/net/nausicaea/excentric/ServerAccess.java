package net.nausicaea.excentric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;

public final class ServerAccess {
	private static MinecraftServer INSTANCE;

	private ServerAccess() {
	}

	public static void register() {
		ServerLifecycleEvents.SERVER_STARTED.register(server -> INSTANCE = server);
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> INSTANCE = null);
	}

	public static Optional<MinecraftServer> get() {
		return Optional.ofNullable(INSTANCE);
	}
}
