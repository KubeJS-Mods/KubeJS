package com.latmod.mods.kubejs.events;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IEventHandler
{
	void onEvent(EventJS event);
}