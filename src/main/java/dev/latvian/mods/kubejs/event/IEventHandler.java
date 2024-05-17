package dev.latvian.mods.kubejs.event;

@FunctionalInterface
public interface IEventHandler {
	Object onEvent(KubeEvent event) throws EventExit;
}