package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("ReferenceToMixin")
@Mixin(value = EventHandler.class, remap = false)
public abstract class EventHandlerBridgeMixin {
	@Unique
	public EventHandler cancelable() {
		return ((EventHandlerInvoker) this).callHasResult();
	}

	@Unique
	public boolean post(Object extraId, KubeEvent event) {
		return ((EventHandlerInvoker) this).callPost(event, extraId).interruptFalse();
	}

	@Unique
	public boolean post(KubeEvent event) {
		return ((EventHandlerInvoker) this).callPost(event, null).interruptFalse();
	}
}
