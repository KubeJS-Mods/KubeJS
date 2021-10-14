package dev.latvian.kubejs.script;


import dev.architectury.architectury.event.Event;
import dev.architectury.architectury.event.EventFactory;

/**
 * @author LatvianModder
 */
public class ScriptsLoadedEvent {
	public static final Event<Runnable> EVENT = EventFactory.createLoop(Runnable.class);
}