package dev.latvian.mods.kubejs.event;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IEventHandler {
	void onEvent(EventJS event);
}