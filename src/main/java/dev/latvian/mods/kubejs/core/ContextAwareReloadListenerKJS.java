package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;

@RemapPrefixForJS("kjs$")
public interface ContextAwareReloadListenerKJS {
	default ConditionalOps<JsonElement> kjs$makeConditionalOps() {
		throw new NoMixinException();
	}
}
