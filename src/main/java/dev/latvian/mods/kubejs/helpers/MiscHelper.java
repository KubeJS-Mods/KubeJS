package dev.latvian.mods.kubejs.helpers;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.network.IContainerFactory;

import java.util.function.Supplier;

public enum MiscHelper {
	INSTANCE;

	public static MiscHelper get() {
		return INSTANCE;
	}

	public void setModName(PlatformWrapper.ModInfo info, String name) {
		try {
			var mc = ModList.get().getModContainerById(info.getId());

			if (mc.isPresent() && mc.get().getModInfo() instanceof ModInfo i) {
				var field = ModInfo.class.getDeclaredField("displayName");
				field.setAccessible(true);
				field.set(i, name);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean isDataGen() {
		return ModLoader.isDataGenRunning();
	}

	public long ingotFluidAmount() {
		return 90;
	}

	public long bottleFluidAmount() {
		return 250;
	}

	public CreativeModeTab creativeModeTab(Component name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator content) {
		return CreativeModeTab.builder().title(name).icon(icon).displayItems(content).build();
	}

	public MenuType<KubeJSMenu> createMenuType() {
		return new MenuType<>((IContainerFactory<KubeJSMenu>) KubeJSMenu::new, FeatureFlags.VANILLA_SET);
	}
}
