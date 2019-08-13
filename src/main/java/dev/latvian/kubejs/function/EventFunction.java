package dev.latvian.kubejs.function;

import dev.latvian.kubejs.events.IEventHandler;

/**
 * @author LatvianModder
 */
@FunctionalInterface
public interface EventFunction
{
	void func(String id, IEventHandler handler);
}