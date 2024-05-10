package dev.latvian.mods.kubejs.item.creativetab;

import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.helpers.MiscHelper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KubeJSCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KubeJS.MOD_ID);

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			REGISTER.register("tab", () -> MiscHelper.get().creativeModeTab(
				Component.literal("KubeJS"),
				() -> {
					var is = ItemStackJS.of(CommonProperties.get().creativeModeTabIcon);
					return is.isEmpty() ? Items.PURPLE_DYE.getDefaultInstance() : is;
				},
				(params, output) -> {
					for (var b : RegistryInfo.ITEM) {
						output.accept(b.get().getDefaultInstance());
					}
				}
			));
		}
	}
}
