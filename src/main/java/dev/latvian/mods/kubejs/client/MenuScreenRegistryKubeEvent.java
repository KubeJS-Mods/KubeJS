package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.gui.KubeJSMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuScreenRegistryKubeEvent implements ClientKubeEvent {
	private final RegisterMenuScreensEvent event;
	private final Set<MenuType<?>> registered;

	public MenuScreenRegistryKubeEvent(RegisterMenuScreensEvent event) {
		this.event = event;
		this.registered = new HashSet<>();
	}

	public void register(MenuType<?> type, MenuScreens.ScreenConstructor constructor) {
		event.register(type, constructor);
		registered.add(type);
	}

	public boolean hasRegistered(MenuType<?> type) {
		return registered.contains(type);
	}

	public void registerKubeJSScreen(Consumer<ScriptedKubeJSScreenBuilder> callback) {
		registerKubeJSScreen(KubeJSMenus.MENU.get(), callback);
	}

	public void registerKubeJSScreen(MenuType<? extends KubeJSMenu> type, Consumer<ScriptedKubeJSScreenBuilder> callback) {
		var builder = new ScriptedKubeJSScreenBuilder();
		callback.accept(builder);
		var config = builder.build();
		register((MenuType<?>) type, (MenuScreens.ScreenConstructor<KubeJSMenu, ScriptedKubeJSScreen>) (menu, inventory, title) -> new ScriptedKubeJSScreen(menu, inventory, title, config));
	}
}
