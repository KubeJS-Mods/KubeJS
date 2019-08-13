package dev.latvian.kubejs;

import dev.latvian.kubejs.events.EventJS;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.BiConsumer;

/**
 * @author LatvianModder
 */
public class KubeJSEventRegistryEvent extends Event
{
	private final BiConsumer<String, Class> callback;

	public KubeJSEventRegistryEvent(BiConsumer<String, Class> c)
	{
		callback = c;
	}

	public void register(String event, Class<? extends EventJS> eventClass)
	{
		callback.accept(event, eventClass);
	}
}