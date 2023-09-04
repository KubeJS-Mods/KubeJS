package dev.latvian.mods.kubejs.item;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class KubeJSCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(KubeJS.MOD_ID, Registries.CREATIVE_MODE_TAB);

	public static final CreativeModeTab KUBEJS_TAB = CreativeTabRegistry.create(KubeJSCreativeTabs::initBuiltinTab);

	public static void initBuiltinTab(CreativeModeTab.Builder builder) {
		builder.title(Component.literal("KubeJS"));

		builder.icon(() -> {
			var is = ItemStackJS.of(CommonProperties.get().creativeModeTabIcon);
			return is.isEmpty() ? Items.PURPLE_DYE.getDefaultInstance() : is;
		});

		builder.displayItems((itemDisplayParameters, output) -> {
			output.accept(Items.APPLE.getDefaultInstance());

			for (var b : RegistryInfo.ITEM) {
				output.accept(((Item) b.get()).getDefaultInstance());
			}
		});
	}

	public static void init() {
		CREATIVE_TABS.register("tab", () -> KUBEJS_TAB);
	}
}
