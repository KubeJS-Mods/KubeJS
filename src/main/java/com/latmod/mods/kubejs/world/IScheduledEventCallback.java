package com.latmod.mods.kubejs.world;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface IScheduledEventCallback
{
	void onCallback(ScheduledEvent callback);
}