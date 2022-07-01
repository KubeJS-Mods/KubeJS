package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import me.shedaniel.rei.api.common.entry.type.EntryType;

import java.util.function.Consumer;

@FunctionalInterface
public interface KubeJSAddREIWrapperEvent {
	Event<Consumer<KubeJSAddREIWrapperEvent>> EVENT = EventFactory.createConsumerLoop();

	void addWrappers(EntryType<?> type, EntryWrapper wrapper);
}
