package dev.latvian.mods.kubejs.platform;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import dev.latvian.mods.kubejs.util.Lazy;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;

public interface MiscPlatformHelper {
	Lazy<MiscPlatformHelper> INSTANCE = Lazy.serviceLoader(MiscPlatformHelper.class);

	static MiscPlatformHelper get() {
		return INSTANCE.get();
	}

	void setModName(PlatformWrapper.ModInfo info, String name);

	MobCategory getMobCategory(String name);

	boolean isDataGen();

	long ingotFluidAmount();

	long bottleFluidAmount();

	MenuType<KubeJSMenu> createMenuType();
}
