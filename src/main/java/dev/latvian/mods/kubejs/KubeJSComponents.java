package dev.latvian.mods.kubejs;

import dev.latvian.mods.rhino.Context;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface KubeJSComponents {
	DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, KubeJS.MOD_ID);

	static void init() {
	}

	static DataComponentMap mapOf(Context cx, Object o) {
		return null;
	}

	static DataComponentPatch patchOf(Context cx, Object o) {
		return null;
	}

	static String mapToString(Context cx, DataComponentMap map) {
		return "[]";
	}

	static String patchToString(Context cx, DataComponentPatch patch) {
		return "[]";
	}
}
