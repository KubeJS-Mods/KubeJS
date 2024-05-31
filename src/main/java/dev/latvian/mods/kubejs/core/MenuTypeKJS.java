package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.world.inventory.MenuType;

public interface MenuTypeKJS extends RegistryObjectKJS<MenuType<?>> {
	@Override
	default RegistryInfo<MenuType<?>> kjs$getKubeRegistry() {
		return RegistryInfo.MENU;
	}
}
