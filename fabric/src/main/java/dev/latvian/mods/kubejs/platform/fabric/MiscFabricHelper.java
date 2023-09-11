package dev.latvian.mods.kubejs.platform.fabric;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;
import dev.latvian.mods.kubejs.script.PlatformWrapper;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class MiscFabricHelper implements MiscPlatformHelper {
	private Boolean dataGen;

	@Override
	public void setModName(PlatformWrapper.ModInfo info, String name) {
		try {
			var mc = FabricLoader.getInstance().getModContainer(info.getId());

			if (mc.isPresent()) {
				var meta = mc.get().getMetadata();
				var field = meta.getClass().getDeclaredField("name");
				field.setAccessible(true);
				field.set(meta, name);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public MobCategory getMobCategory(String name) {
		// safe cast, mojang just specified too general of a type
		return ((StringRepresentable.EnumCodec<MobCategory>) MobCategory.CODEC).byName(name);
	}

	@Override
	public boolean isDataGen() {
		if (dataGen == null) {
			// FabricDataGenHelper.ENABLED
			dataGen = System.getProperty("fabric-api.datagen") != null;
		}

		return dataGen;
	}

	@Override
	public long ingotFluidAmount() {
		return FluidConstants.INGOT;
	}

	@Override
	public long bottleFluidAmount() {
		return FluidConstants.BOTTLE;
	}

	@Override
	public CreativeModeTab creativeModeTab(Component name, Supplier<ItemStack> icon, CreativeModeTab.DisplayItemsGenerator content) {
		return FabricItemGroup.builder().title(name).icon(icon).displayItems(content).build();
	}

	@Override
	public MenuType<KubeJSMenu> createMenuType() {
		return new ExtendedScreenHandlerType<>(KubeJSMenu::new);
	}
}
