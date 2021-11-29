package dev.latvian.mods.kubejs.server;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IScheduledEventCallback {
	void onCallback(ScheduledEvent callback);
}