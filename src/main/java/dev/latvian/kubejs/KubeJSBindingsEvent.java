package dev.latvian.kubejs;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
public class KubeJSBindingsEvent extends Event
{
	private final BiConsumer<String, Object> callback;

	public KubeJSBindingsEvent(BiConsumer<String, Object> c)
	{
		callback = c;
	}

	public void add(String name, Object value)
	{
		callback.accept(name, value);
	}
}