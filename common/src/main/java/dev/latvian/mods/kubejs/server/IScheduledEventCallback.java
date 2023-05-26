package dev.latvian.mods.kubejs.server;

@FunctionalInterface
public interface IScheduledEventCallback {
	void onCallback(ScheduledEvent callback);
}