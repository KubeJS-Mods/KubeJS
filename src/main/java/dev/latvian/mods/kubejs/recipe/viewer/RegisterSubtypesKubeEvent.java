package dev.latvian.mods.kubejs.recipe.viewer;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import net.minecraft.core.component.DataComponentType;

import java.util.List;

public interface RegisterSubtypesKubeEvent extends KubeEvent {
	void register(Context cx, Object filter, SubtypeInterpreter interpreter);

	default void useComponents(Context cx, Object filter) {
		useComponents(cx, filter, List.of());
	}

	void useComponents(Context cx, Object filter, List<DataComponentType<?>> components);
}
