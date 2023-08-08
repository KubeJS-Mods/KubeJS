package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;

public interface KubeJSCreativeTabs {
	DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(KubeJS.MOD_ID, Registries.CREATIVE_MODE_TAB);

	RegistrySupplier<CreativeModeTab> KUBEJS_TAB = CREATIVE_TABS.register("tab", () -> CreativeTabRegistry.create((builder) -> {
		builder.icon(Items.PURPLE_DYE::getDefaultInstance); // TODO: add configurable icon
	}));
}
