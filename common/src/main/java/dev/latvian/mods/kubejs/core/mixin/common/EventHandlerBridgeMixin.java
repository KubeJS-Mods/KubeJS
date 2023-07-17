package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings("ReferenceToMixin")
@Mixin(value = EventHandler.class, remap = false)
public abstract class EventHandlerBridgeMixin {
	public EventHandler cancelable() {
		return ((EventHandlerInvoker) this).callHasResult();
	}

	public boolean post(Object extraId, EventJS event) {
		return ((EventHandlerInvoker) this).callPost(event, extraId).interruptFalse();
	}
}
