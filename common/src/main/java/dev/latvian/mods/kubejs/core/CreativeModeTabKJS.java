package dev.latvian.mods.kubejs.core;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface CreativeModeTabKJS {
	void kjs$setDisplayName(Component component);

	void kjs$setIcon(ItemStack icon);
}
