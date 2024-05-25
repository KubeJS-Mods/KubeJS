package dev.latvian.mods.kubejs.core.mixin;

import dev.latvian.mods.kubejs.core.CraftingContainerKJS;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CraftingContainer.class)
public interface CraftingContainerMixin extends CraftingContainerKJS {
	@Override
	@Nullable
	default AbstractContainerMenu kjs$getMenu() {
		return this instanceof TransientCraftingContainer container ? container.menu : null;
	}
}
