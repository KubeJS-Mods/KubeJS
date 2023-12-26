package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface MiscPlatformHelper {
	Lazy<MiscPlatformHelper> INSTANCE = Lazy.serviceLoader(MiscPlatformHelper.class);

	static MiscPlatformHelper get() {
		return INSTANCE.get();
	}

	void setModName(PlatformWrapper.ModInfo info, String name);

	boolean isDataGen();

	long ingotFluidAmount();

	long bottleFluidAmount();

	CreativeModeTab creativeModeTab(Component name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator content);

	MenuType<KubeJSMenu> createMenuType();
}
