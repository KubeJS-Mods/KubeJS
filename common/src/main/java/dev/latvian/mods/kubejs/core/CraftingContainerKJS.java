package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS("kjs$")
public interface CraftingContainerKJS {
	@Nullable
	default AbstractContainerMenu kjs$getMenu() {
		throw new NoMixinException();
	}
}
