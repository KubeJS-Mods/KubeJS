package com.latmod.mods.worldjs.mod;

import com.latmod.mods.worldjs.events.EventJS;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
public class WorldJSEventRegistryEvent extends Event
{
	private final BiConsumer<String, Class> callback;

	public WorldJSEventRegistryEvent(BiConsumer<String, Class> c)
	{
		callback = c;
	}

	public void register(String event, Class<? extends EventJS> eventClass)
	{
		callback.accept(event, eventClass);
	}
}