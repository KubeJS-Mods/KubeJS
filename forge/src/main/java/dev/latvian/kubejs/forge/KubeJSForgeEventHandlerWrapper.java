package dev.latvian.kubejs.forge;

import net.minecraftforge.eventbus.api.Event;

import java.util.function.Consumer;

@FunctionalInterface
public interface KubeJSForgeEventHandlerWrapper extends Consumer<Event> {
}
