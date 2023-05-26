package dev.latvian.mods.kubejs.event;

@FunctionalInterface
public interface IEventHandler {
	void onEvent(EventJS event);
}