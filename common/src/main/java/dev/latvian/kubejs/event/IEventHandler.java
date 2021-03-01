package dev.latvian.kubejs.event;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IEventHandler {
	void onEvent(EventJS event);
}