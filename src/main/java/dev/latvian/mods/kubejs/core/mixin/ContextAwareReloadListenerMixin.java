package dev.latvian.mods.kubejs.core.mixin;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.ContextAwareReloadListenerKJS;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.resource.ContextAwareReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContextAwareReloadListener.class)
public abstract class ContextAwareReloadListenerMixin implements ContextAwareReloadListenerKJS {
	@Shadow
	protected abstract ConditionalOps<JsonElement> makeConditionalOps();

	@Override
	public ConditionalOps<JsonElement> kjs$makeConditionalOps() {
		return makeConditionalOps();
	}

	@Shadow
	protected abstract ICondition.IContext getContext();

	@Override
	public ICondition.IContext kjs$getContext() {
		return getContext();
	}
}
