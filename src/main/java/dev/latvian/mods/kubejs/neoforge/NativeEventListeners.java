package dev.latvian.mods.kubejs.neoforge;

import dev.latvian.mods.rhino.type.TypeInfo;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class NativeEventListeners implements Consumer<Event> {
	public static final TypeInfo EVENT_CONSUMER_TYPE = TypeInfo.RAW_CONSUMER.withParams(TypeInfo.of(Event.class));

	public record Key(Class<?> eventClass, EventPriority priority) {
		@Override
		public boolean equals(Object o) {
			return o == this || o instanceof Key k && k.eventClass == eventClass && k.priority == priority;
		}
	}

	public final List<Consumer<Event>> listeners;

	public NativeEventListeners() {
		this.listeners = new LinkedList<>();
	}

	@Override
	public void accept(Event event) {
		for (var listener : listeners) {
			listener.accept(event);
		}
	}
}
