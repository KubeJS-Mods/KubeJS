package com.latmod.mods.worldjs.events;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IEventHandler
{
	void onEvent(EventJS event);
}