package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EventHandler.class, remap = false)
public interface EventHandlerInvoker {

	@Invoker(remap = false)
	EventHandler callHasResult();

	@Invoker(remap = false)
	EventResult callPost(KubeEvent event, @Nullable Object extraId);
}
