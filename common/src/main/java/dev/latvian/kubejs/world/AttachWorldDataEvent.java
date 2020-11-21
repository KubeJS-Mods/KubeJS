package dev.latvian.kubejs.world;

import dev.latvian.kubejs.script.AttachDataEvent;
import dev.latvian.kubejs.script.DataType;
import me.shedaniel.architectury.event.Event;
import me.shedaniel.architectury.event.EventFactory;

import java.util.function.Consumer;

/**
 * @author LatvianModder
 */
public class AttachWorldDataEvent extends AttachDataEvent<WorldJS>
{
	public static final Event<Consumer<AttachWorldDataEvent>> EVENT = EventFactory.createConsumerLoop(AttachWorldDataEvent.class);

	public AttachWorldDataEvent(WorldJS w)
	{
		super(DataType.WORLD, w);
	}
}