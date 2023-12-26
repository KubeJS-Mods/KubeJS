package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.CreativeModeTabKJS;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin implements CreativeModeTabKJS {
	@Shadow
	@Final
	@Mutable
	private Component displayName;

	@Shadow
	private ItemStack iconItemStack;

	@Override
	public void kjs$setDisplayName(Component component) {
		displayName = component;
	}

	@Override
	public void kjs$setIcon(ItemStack icon) {
		iconItemStack = icon;
	}
}
