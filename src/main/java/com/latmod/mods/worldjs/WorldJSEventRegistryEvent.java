package com.latmod.mods.worldjs;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class WorldJSEventRegistryEvent extends Event
{
	private final Consumer<String> callback;

	public WorldJSEventRegistryEvent(Consumer<String> c)
	{
		callback = c;
	}

	public void register(String event)
	{
		callback.accept(event);
	}
}