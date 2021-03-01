package dev.latvian.kubejs.script;


import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

/**
 * @author LatvianModder
 */
public class ScriptsLoadedEvent {
	public static final Event<Runnable> EVENT = EventFactory.createLoop(Runnable.class);
}