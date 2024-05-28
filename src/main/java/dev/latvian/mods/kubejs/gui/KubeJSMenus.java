package dev.latvian.mods.kubejs.gui;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.helpers.MiscHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface KubeJSMenus {
	DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, KubeJS.MOD_ID);

	Supplier<MenuType<KubeJSMenu>> MENU = REGISTRY.register("menu", () -> MiscHelper.get().createMenuType());
}
