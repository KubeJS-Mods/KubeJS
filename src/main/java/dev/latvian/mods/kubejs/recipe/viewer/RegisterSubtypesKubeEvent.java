package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.component.DataComponentType;

public interface RegisterSubtypesKubeEvent extends KubeEvent {
	void register(Context cx, Object filter, SubtypeInterpreter interpreter);

	void useComponents(Context cx, Object filter);

	void useComponents(Context cx, Object filter, DataComponentType<?>[] components);
}
