package dev.latvian.mods.kubejs.forge;

import net.minecraftforge.eventbus.api.GenericEvent;

import java.util.function.Consumer;

@FunctionalInterface
public interface GenericForgeEventConsumer extends Consumer<GenericEvent<?>> {
}