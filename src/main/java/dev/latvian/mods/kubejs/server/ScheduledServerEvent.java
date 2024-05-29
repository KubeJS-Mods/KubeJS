package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ScheduledServerEvent extends ScheduledEvents.ScheduledEvent {
	public static final ScheduledEvents EVENTS = new ScheduledEvents(ScheduledServerEvent::new);

	public MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
}