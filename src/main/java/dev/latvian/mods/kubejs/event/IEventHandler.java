package dev.latvian.mods.kubejs.event;

@FunctionalInterface
public interface IEventHandler {
	Object onEvent(EventJS event) throws EventExit;
}