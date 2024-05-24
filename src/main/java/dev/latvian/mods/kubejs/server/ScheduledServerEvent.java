package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ScheduledServerEvent extends ScheduledEvents.ScheduledEvent {
	public static final ScheduledEvents EVENTS = new ScheduledEvents(() -> new ScheduledServerEvent(ServerLifecycleHooks.getCurrentServer()));

	public final MinecraftServer server;

	public ScheduledServerEvent(MinecraftServer server) {
		this.server = server;
	}
}