package dev.latvian.mods.kubejs.core;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.MenuType;

public interface MenuTypeKJS extends RegistryObjectKJS<MenuType<?>> {
	@Override
	default ResourceKey<Registry<MenuType<?>>> kjs$getRegistryId() {
		return Registries.MENU;
	}

	@Override
	default Registry<MenuType<?>> kjs$getRegistry() {
		return BuiltInRegistries.MENU;
	}
}
