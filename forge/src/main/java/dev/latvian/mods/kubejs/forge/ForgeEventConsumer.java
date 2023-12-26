package dev.latvian.mods.kubejs.forge;

import net.neoforged.bus.api.Event;

import java.util.function.Consumer;

@FunctionalInterface
public interface ForgeEventConsumer extends Consumer<Event> {
}