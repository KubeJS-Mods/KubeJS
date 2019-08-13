package dev.latvian.kubejs.events;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IEventHandler
{
	void onEvent(EventJS event);
}