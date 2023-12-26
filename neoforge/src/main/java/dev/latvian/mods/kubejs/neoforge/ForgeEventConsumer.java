package dev.latvian.mods.kubejs.neoforge;

import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

@FunctionalInterface
public interface ForgeEventConsumer extends Consumer<Event> {
}