package dev.latvian.mods.kubejs.platform.forge;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.network.IContainerFactory;

public class MiscForgeHelper implements MiscPlatformHelper {
	@Override
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

	@Override
	public MobCategory getMobCategory(String name) {
		return MobCategory.byName(name);
	}

	@Override
	public boolean isDataGen() {
		return ModLoader.isDataGenRunning();
	}

	@Override
	public long ingotFluidAmount() {
		return 90;
	}

	@Override
	public long bottleFluidAmount() {
		return 250;
	}

	@Override
	public MenuType<KubeJSMenu> createMenuType() {
		return new MenuType<>((IContainerFactory<KubeJSMenu>) KubeJSMenu::new);
	}
}
