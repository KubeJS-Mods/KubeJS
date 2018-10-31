package com.latmod.mods.worldjs.mod;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
public class WorldJSBindingsEvent extends Event
{
	private final BiConsumer<String, Object> callback;

	public WorldJSBindingsEvent(BiConsumer<String, Object> c)
	{
		callback = c;
	}

	public void add(String name, Object value)
	{
		callback.accept(name, value);
	}
}