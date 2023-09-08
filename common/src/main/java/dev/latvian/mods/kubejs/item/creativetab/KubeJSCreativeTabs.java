package dev.latvian.mods.kubejs.item.creativetab;

import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class KubeJSCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(KubeJS.MOD_ID, Registries.CREATIVE_MODE_TAB);

	public static void init() {
		if (!CommonProperties.get().serverOnly) {
			CREATIVE_TABS.register("tab", () -> MiscPlatformHelper.get().creativeModeTab(
				Component.literal("KubeJS"),
				(CreativeTabIconSupplier) () -> ItemStackJS.of(CommonProperties.get().creativeModeTabIcon),
				(CreativeTabContentSupplier) showRestrictedItems -> {
					var list = new ArrayList<ItemStack>();

					for (var b : RegistryInfo.ITEM) {
						list.add(((Item) b.get()).getDefaultInstance());
					}

					return list.toArray(ItemStackJS.EMPTY_ARRAY);
				}
			));

			CREATIVE_TABS.register();
		}
	}
}
