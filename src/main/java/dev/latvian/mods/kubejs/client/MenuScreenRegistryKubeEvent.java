package dev.latvian.mods.kubejs.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuScreenRegistryKubeEvent implements ClientKubeEvent {
	private final RegisterMenuScreensEvent event;

	public MenuScreenRegistryKubeEvent(RegisterMenuScreensEvent event) {
		this.event = event;
	}

	public void register(MenuType<?> type, MenuScreens.ScreenConstructor constructor) {
		event.register(type, constructor);
	}
}
