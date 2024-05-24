package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.util.ScheduledEvents;
import net.minecraft.client.Minecraft;

public class ScheduledClientEvent extends ScheduledEvents.ScheduledEvent {
	public static final ScheduledEvents EVENTS = new ScheduledEvents(() -> new ScheduledClientEvent(Minecraft.getInstance()));

	public final Minecraft client;

	public ScheduledClientEvent(Minecraft client) {
		this.client = client;
	}
}