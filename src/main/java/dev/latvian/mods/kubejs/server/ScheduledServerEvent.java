package dev.latvian.mods.kubejs.server;

import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jspecify.annotations.Nullable;

public class ScheduledServerEvent extends ScheduledEvents.ScheduledEvent {
	public static final ScheduledEvents EVENTS = new ScheduledEvents(ScheduledServerEvent::new);

	@Nullable
	public MinecraftServer getServer() {
		return ServerLifecycleHooks.getCurrentServer();
	}
}