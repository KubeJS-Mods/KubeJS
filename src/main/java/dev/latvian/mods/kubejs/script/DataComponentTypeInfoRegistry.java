package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.rhino.type.TypeInfo;
import net.minecraft.core.component.DataComponentType;

@FunctionalInterface
public interface DataComponentTypeInfoRegistry {
	void register(DataComponentType<?> type, TypeInfo typeInfo);
}
