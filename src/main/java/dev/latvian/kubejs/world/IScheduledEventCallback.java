package dev.latvian.kubejs.world;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IScheduledEventCallback
{
	void onCallback(ScheduledEvent callback);
}