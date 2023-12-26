package dev.latvian.mods.kubejs.neoforge;

import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

@FunctionalInterface
public interface NativeEventConsumer extends Consumer<Event> {
}