package dev.latvian.mods.kubejs;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface KubeJSComponents {
	DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, KubeJS.MOD_ID);

	static void init() {
	}
}
