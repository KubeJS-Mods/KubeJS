package dev.latvian.kubejs.server;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IScheduledEventCallback {
	void onCallback(ScheduledEvent callback);
}